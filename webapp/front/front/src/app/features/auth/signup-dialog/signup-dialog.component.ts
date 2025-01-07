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
} from '@shared/index';
import { environment } from '../../../../environments/environment';
import { AuthService, ToastService, UserSessionService } from '@core/index';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup-dialog',
  templateUrl: './signup-dialog.component.html',
})
export class SignupDialogComponent implements OnInit {
  @Input() showSignupDialog: boolean = false;
  @Output() showSignupDialogChange = new EventEmitter<boolean>();

  private apiServerUrl = environment.apiBaseUrl;

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
    private professionService: ProfessionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // 1) Load combos
    this.getNeighborhoods();
    this.getLanguages();
    this.getProfessions();

    // 2) Initialize neighbor form
    this.signupForm = this.fb.group({
      neighborhoodId: ['', Validators.required],
      name: ['', Validators.required],
      surname: ['', Validators.required],
      mail: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      identification: [
        null,
        [
          Validators.required,
          Validators.pattern('[0-9]*'),
          Validators.min(1),
          Validators.max(99999999),
        ],
      ],
      language: ['', Validators.required],
    });

    // 3) Initialize service/worker form
    this.serviceForm = this.fb.group({
      businessName: ['', Validators.required],
      professionIds: [[], Validators.required], // we store an array of IDs
      w_name: ['', Validators.required],
      w_surname: ['', Validators.required],
      w_mail: ['', [Validators.required, Validators.email]],
      w_password: ['', Validators.required],
      address: ['', Validators.required],
      w_identification: [
        null,
        [Validators.required, Validators.min(1), Validators.max(99999999)],
      ],
      w_language: ['', Validators.required],
      phoneNumber: ['', Validators.required],
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
            .login(signupValues.mail, signupValues.password, false)
            .subscribe({
              next: (success) => {
                this.loading = false;
                if (success) {
                  const userRole = this.userSessionService.getCurrentUserRole();
                  if (
                    userRole === 'UNVERIFIED_NEIGHBOR' ||
                    userRole === 'UNVERIFIED_WORKER'
                  ) {
                    this.router.navigate(['/unverified']).then(() => {
                      this.closeSignupDialog();
                    });
                  } else {
                    this.router.navigate(['/posts']).then(() => {
                      this.closeSignupDialog();
                    });
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
    this.showSignupDialog = false;
    this.showSignupDialogChange.emit(this.showSignupDialog);
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
