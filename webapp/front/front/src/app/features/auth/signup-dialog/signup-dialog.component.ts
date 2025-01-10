import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
  ElementRef,
  HostListener,
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  Neighborhood,
  NeighborhoodService,
  UserService,
  LanguageService,
  Language,
  ProfessionService,
  Profession,
  Roles,
  LinkKey,
} from '@shared/index';

import {
  AuthService,
  HateoasLinksService,
  ToastService,
  UserSessionService,
} from '@core/index';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup-dialog',
  templateUrl: './signup-dialog.component.html',
})
export class SignupDialogComponent implements OnInit {
  @Input() showSignupDialog: boolean = false;
  @Output() showSignupDialogChange = new EventEmitter<boolean>();

  // Toggle for neighbor vs service
  selectedOption: 'neighbor' | 'service' = 'neighbor';

  // Common data
  languages: Language[] = [];
  neighborhoodsList: Neighborhood[] = [];

  // ========== NEIGHBOR SIGNUP ==========
  signupForm!: FormGroup;

  // ========== SERVICE (WORKER) SIGNUP ==========
  serviceForm!: FormGroup;
  loading = false;

  // Professions available
  professionOptions: Profession[] = [];

  // ----------- ADDED: Professions multi-select ----------
  // Track selected profession IDs
  selectedProfessionIds: string[] = [];

  // Whether the drop-down is open
  isProfessionSelectOpen = false;

  // For referencing the select-button element (so we can detect outside clicks)
  @ViewChild('professionSelectBtn') professionSelectBtnRef!: ElementRef;
  // -----------------------------------------------------

  constructor(
    private fb: FormBuilder,
    private neighborhoodService: NeighborhoodService,
    private toastService: ToastService,
    private userService: UserService,
    private languageService: LanguageService,
    private userSessionService: UserSessionService,
    private authService: AuthService,
    private linkStorage: HateoasLinksService,
    private professionService: ProfessionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // 1) Load combos
    this.getNeighborhoods();
    this.getLanguages();
    this.getProfessions();

    // ========== NEIGHBOR SIGNUP FORM ==========
    this.signupForm = this.fb.group({
      neighborhoodId: ['', Validators.required],
      name: [
        '',
        [
          Validators.required,
          Validators.pattern('^[a-zA-ZÀ-ž\\s]+$'), // Example: Only letters/spaces
        ],
      ],
      surname: [
        '',
        [Validators.required, Validators.pattern('^[a-zA-ZÀ-ž\\s]+$')],
      ],
      mail: [
        '',
        [
          Validators.required,
          Validators.email, // Basic email check
        ],
      ],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          // Simple example pattern:
          // At least one uppercase, one lowercase, and a digit
          Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$'),
        ],
      ],
      identification: [
        null,
        [
          Validators.required,
          Validators.pattern('^[1-9][0-9]{6,7}$'), // 7 to 8 digits, no leading zeros
        ],
      ],
      language: ['', Validators.required],
    });

    // ========== SERVICE SIGNUP FORM ==========
    this.serviceForm = this.fb.group({
      businessName: ['', Validators.required],
      professionIds: [[], Validators.required],
      w_name: [
        '',
        [Validators.required, Validators.pattern('^[a-zA-ZÀ-ž\\s]+$')],
      ],
      w_surname: [
        '',
        [Validators.required, Validators.pattern('^[a-zA-ZÀ-ž\\s]+$')],
      ],
      w_mail: ['', [Validators.required, Validators.email]],
      w_password: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$'),
        ],
      ],
      w_address: ['', Validators.required],
      w_identification: [
        null,
        [
          Validators.required,
          Validators.pattern('^[1-9][0-9]{6,7}$'), // 7 to 8 digits, no leading zeros
        ],
      ],
      w_language: ['', Validators.required],
      phoneNumber: [
        '',
        [
          Validators.required,
          // Example pattern for phone: +54 9 11 1234 5678
          // This is just an example - adapt to your country or remove if not needed
          Validators.pattern('^\\+?\\d[\\d\\s()-]+\\d$'),
        ],
      ],
    });
  }

  // =========================================
  // ============== NEIGHBOR FLOW ============
  // =========================================
  trySignup(): void {
    if (this.signupForm.invalid) {
      this.toastService.showToast(
        'Please fill out all required fields.',
        'error'
      );
      return;
    }

    this.loading = true;
    const signupValues = this.signupForm.value;

    // Example neighbor creation
    this.userService
      .createUser(
        signupValues.neighborhoodId,
        signupValues.name,
        signupValues.surname,
        signupValues.password,
        signupValues.mail,
        signupValues.language,
        signupValues.identification
      )
      .subscribe({
        next: () => {
          this.toastService.showToast(
            'Neighbor signup successful! Logging you in...',
            'success'
          );
          // Attempt auto-login
          this.authService
            .login(signupValues.mail, signupValues.password)
            .subscribe({
              next: (success) => {
                this.loading = false;
                if (success) {
                  // Get the user's role from UserSessionService
                  const userRole = this.authService.getCurrentRole();
                  console.log('hola' + userRole);
                  switch (userRole) {
                    case Roles.WORKER:
                      const workerUrl = this.linkStorage.getLink(
                        LinkKey.USER_WORKER
                      );
                      this.router
                        .navigate(['services', 'profile', workerUrl])
                        .then(() => this.closeSignupDialog());
                      break;

                    case Roles.NEIGHBOR:
                    case Roles.ADMINISTRATOR:
                      const announcementesChannelUrl = this.linkStorage.getLink(
                        LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL
                      );
                      const nonePostStatus = this.linkStorage.getLink(
                        LinkKey.NONE_POST_STATUS
                      );
                      this.router
                        .navigate(['posts'], {
                          queryParams: {
                            inChannel: announcementesChannelUrl,
                            withStatus: nonePostStatus,
                          },
                        })
                        .then(() => this.closeSignupDialog());
                      break;

                    case Roles.UNVERIFIED_NEIGHBOR:
                    case Roles.UNVERIFIED_WORKER:
                      this.router
                        .navigate(['unverified'])
                        .then(() => this.closeSignupDialog());

                      break;
                    case Roles.REJECTED:
                      this.router
                        .navigate(['rejected'])
                        .then(() => this.closeSignupDialog());
                      break;

                    default:
                      // Fallback for unexpected or null roles
                      this.router
                        .navigate(['not-found'])
                        .then(() => this.closeSignupDialog());
                      break;
                  }
                } else {
                  this.toastService.showToast(
                    'Login failed after signup, please try manually.',
                    'error'
                  );
                  this.closeSignupDialog();
                }
              },
              error: (err) => {
                console.error('Automatic login error:', err);
                this.loading = false;
                this.toastService.showToast(
                  'Login failed after signup, please try manually.',
                  'error'
                );
                this.closeSignupDialog();
              },
            });
        },
        error: (error) => {
          console.error('Error during neighbor signup:', error);
          this.loading = false;
          this.toastService.showToast(
            'Signup failed. Please try again.',
            'error'
          );
        },
      });
  }

  // =========================================
  // ============ SERVICE/WORKER FLOW ========
  // =========================================
  submitServiceForm(): void {
    if (this.serviceForm.invalid) {
      this.toastService.showToast(
        'Please fill out all required fields.',
        'error'
      );
      return;
    }
    this.loading = true;

    const workerValues = this.serviceForm.value;

    // Example call; adapt to your actual worker endpoint
    // this.userService.createWorker(...).subscribe({
    //   next: () => {
    //     this.toastService.showToast('Service provider signup successful!', 'success');
    //     this.closeSignupDialog();
    //   },
    //   error: (error) => {
    //     console.error('Error during service signup:', error);
    //     this.toastService.showToast('Signup failed. Please try again.', 'error');
    //   }
    // });
  }

  // =======================
  // = Profession Multi-Select Logic (mirroring your Neighborhoods approach)
  // =======================

  /**
   * A computed text to display in the select button
   */
  get professionDisplayText(): string {
    if (this.selectedProfessionIds.length === 0) {
      return 'Select profession';
    }
    // gather the displayNames for the chosen IDs
    const selectedNames = this.professionOptions
      .filter((p) => this.selectedProfessionIds.includes(p.self))
      .map((p) => p.displayName);

    if (selectedNames.length === 1) {
      return selectedNames[0];
    } else {
      return `(${selectedNames.length}) ${selectedNames.join(', ')}`;
    }
  }

  /**
   * Toggle the open/close state of the multi-select
   */
  toggleProfessionSelect(): void {
    this.isProfessionSelectOpen = !this.isProfessionSelectOpen;
  }

  /**
   * Add/remove the clicked profession from the selected array
   * and stop the click from also toggling the entire select.
   */
  toggleProfessionItem(professionId: string, event: MouseEvent): void {
    event.stopPropagation(); // stops the select-btn toggling

    if (this.selectedProfessionIds.includes(professionId)) {
      // Remove
      this.selectedProfessionIds = this.selectedProfessionIds.filter(
        (id) => id !== professionId
      );
    } else {
      // Add
      this.selectedProfessionIds.push(professionId);
    }

    // Update the underlying form control
    this.serviceForm.get('professionIds')?.setValue(this.selectedProfessionIds);
  }

  /**
   * Clicking anywhere outside the .select-btn area will close the dropdown
   * (mirroring the "document:click" logic from your original neighborhoods code).
   */
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (
      this.isProfessionSelectOpen &&
      this.professionSelectBtnRef &&
      !this.professionSelectBtnRef.nativeElement.contains(event.target)
    ) {
      this.isProfessionSelectOpen = false;
    }
  }

  // =========== Common Utility =============
  selectOption(option: 'neighbor' | 'service'): void {
    this.selectedOption = option;
  }

  closeSignupDialog(): void {
    if (this.signupForm.dirty || this.serviceForm.dirty) {
      this.resetForms();
    }

    this.showSignupDialog = false;
    this.showSignupDialogChange.emit(this.showSignupDialog);
  }

  resetForms(): void {
    this.signupForm.reset(); // Clear values and reset validation
    this.signupForm.markAsPristine(); // Mark form as pristine
    this.signupForm.markAsUntouched(); // Reset touched states

    this.serviceForm.reset();
    this.serviceForm.markAsPristine();
    this.serviceForm.markAsUntouched();
  }

  // =========== Data Fetching =============
  getNeighborhoods(): void {
    this.neighborhoodService.getNeighborhoods().subscribe({
      next: (response) => {
        this.neighborhoodsList = response.neighborhoods;
      },
      error: (error) => {
        console.error('Error getting neighborhoods:', error);
        this.toastService.showToast(
          'There was a problem fetching the neighborhoods. Try again.',
          'error'
        );
      },
    });
  }

  getLanguages(): void {
    this.languageService.getLanguages().subscribe({
      next: (languages) => {
        this.languages = languages;
      },
      error: (error) => {
        console.error('Error getting languages:', error);
        this.toastService.showToast(
          'Could not retrieve available languages. Try Again.',
          'error'
        );
      },
    });
  }

  getProfessions(): void {
    this.professionService.getProfessions().subscribe({
      next: (professions) => {
        this.professionOptions = professions;
      },
      error: (error) => {
        console.error('Error getting professions:', error);
        this.toastService.showToast(
          'Could not retrieve professions. Try Again.',
          'error'
        );
      },
    });
  }
}
