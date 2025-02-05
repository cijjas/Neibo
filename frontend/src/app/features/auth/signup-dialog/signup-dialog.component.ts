import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
  ElementRef,
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
  Role,
  LinkKey,
  WorkerService,
} from '@shared/index';

import {
  AuthService,
  HateoasLinksService,
  ToastService,
  UserSessionService,
} from '@core/index';
import { Router } from '@angular/router';
import { map, Observable } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { VALIDATION_CONFIG } from '@shared/constants/validation-config';

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

  // For referencing the select-button element (so we can detect outside clicks)
  @ViewChild('professionSelectBtn') professionSelectBtnRef!: ElementRef;
  // -----------------------------------------------------

  constructor(
    private fb: FormBuilder,
    private neighborhoodService: NeighborhoodService,
    private toastService: ToastService,
    private userService: UserService,
    private languageService: LanguageService,
    private authService: AuthService,
    private linkStorage: HateoasLinksService,
    private professionService: ProfessionService,
    private router: Router,
    private workerService: WorkerService,
    private userSessionService: UserSessionService,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    // 1) Load combos
    this.getNeighborhoods();
    this.getLanguages();

    // ========== NEIGHBOR SIGNUP FORM ==========
    this.signupForm = this.fb.group({
      neighborhood: ['', Validators.required],
      name: [
        '',
        [
          Validators.required,
          Validators.pattern(VALIDATION_CONFIG.PATTERN_LETTERS_AND_SPACES), // Example: Only letters/spaces
        ],
      ],
      surname: [
        '',
        [
          Validators.required,
          Validators.pattern(VALIDATION_CONFIG.PATTERN_LETTERS_AND_SPACES),
        ],
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
          // Validators.minLength(8),
          // Simple example pattern:
          // At least one uppercase, one lowercase, and a digit
          // Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$'),
        ],
      ],
      identification: [
        null,
        [
          Validators.required,
          Validators.pattern(VALIDATION_CONFIG.PATTERN_ID), // 7 to 8 digits, no leading zeros
        ],
      ],
      language: ['', Validators.required],
    });

    // ========== SERVICE SIGNUP FORM ==========
    this.serviceForm = this.fb.group({
      businessName: ['', Validators.required],
      professions: [[], Validators.required],
      w_name: [
        '',
        [
          Validators.required,
          Validators.pattern(VALIDATION_CONFIG.PATTERN_LETTERS_AND_SPACES),
        ],
      ],
      w_surname: [
        '',
        [
          Validators.required,
          Validators.pattern(VALIDATION_CONFIG.PATTERN_LETTERS_AND_SPACES),
        ],
      ],
      w_mail: ['', [Validators.required, Validators.email]],
      w_password: [
        '',
        [
          Validators.required,
          // Validators.minLength(8),
          // Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$'),
        ],
      ],
      w_address: ['', Validators.required],
      w_identification: [
        null,
        [
          Validators.required,
          Validators.pattern(VALIDATION_CONFIG.PATTERN_ID), // 7 to 8 digits, no leading zeros
        ],
      ],
      w_language: ['', Validators.required],
      phoneNumber: [
        '',
        [
          Validators.required,
          // Example pattern for phone: +54 9 11 1234 5678
          // This is just an example - adapt to your country or remove if not needed
          Validators.pattern(VALIDATION_CONFIG.PATTERN_PHONE_NUMBER),
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
        'error',
      );
      return;
    }

    this.loading = true;
    const signupValues = this.signupForm.value;

    // Example neighbor creation
    this.userService
      .createUser(
        signupValues.neighborhood.self,
        signupValues.name,
        signupValues.surname,
        signupValues.password,
        signupValues.mail,
        signupValues.language,
        signupValues.identification,
      )
      .subscribe({
        next: () => {
          this.toastService.showToast(
            this.translate.instant(
              'SIGNUP-DIALOG.NEIGHBOR_SIGNUP_SUCCESSFUL_LOGGING_YOU_IN',
            ),
            'success',
          );
          // Attempt auto-login
          this.authService
            .login(signupValues.mail, signupValues.password)
            .subscribe({
              next: success => {
                this.loading = false;
                if (success) {
                  // Get the user's role from UserSessionService
                  const userRole = this.userSessionService.getCurrentRole();
                  switch (userRole) {
                    case Role.WORKER:
                      const workerUrl = this.linkStorage.getLink(
                        LinkKey.USER_WORKER,
                      );
                      this.router
                        .navigate(['services', 'profile', workerUrl])
                        .then(() => this.closeSignupDialog());
                      break;

                    case Role.NEIGHBOR:
                    case Role.ADMINISTRATOR:
                      const announcementesChannelUrl = this.linkStorage.getLink(
                        LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL,
                      );
                      const nonePostStatus = this.linkStorage.getLink(
                        LinkKey.NONE_POST_STATUS,
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

                    case Role.UNVERIFIED_NEIGHBOR:
                      this.router
                        .navigate(['unverified'])
                        .then(() => this.closeSignupDialog());

                      break;

                    case Role.REJECTED:
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
                    this.translate.instant(
                      'SIGNUP-DIALOG.LOGIN_FAILED_AFTER_SIGNUP_PLEASE_TRY_MANUALLY',
                    ),
                    'error',
                  );
                  this.closeSignupDialog();
                }
              },
              error: err => {
                console.error('Automatic login error:', err);
                this.loading = false;
                this.toastService.showToast(
                  this.translate.instant(
                    'SIGNUP-DIALOG.LOGIN_FAILED_AFTER_SIGNUP_PLEASE_TRY_MANUALLY',
                  ),
                  'error',
                );
                this.closeSignupDialog();
              },
            });
        },
        error: error => {
          console.error('Error during neighbor signup:', error);
          this.loading = false;
          this.toastService.showToast(
            this.translate.instant(
              'SIGNUP-DIALOG.SIGNUP_FAILED_PLEASE_TRY_AGAIN',
            ),
            'error',
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
        this.translate.instant(
          'SIGNUP-DIALOG.PLEASE_FILL_OUT_ALL_REQUIRED_FIELDS',
        ),
        'error',
      );
      return;
    }
    this.loading = true;

    const workerValues = this.serviceForm.value;

    this.workerService
      .createWorker(
        workerValues.w_name,
        workerValues.w_surname,
        workerValues.w_password,
        workerValues.w_mail,
        workerValues.w_language,
        workerValues.w_identification,
        workerValues.professions.map((p: Profession) => p.self),
        workerValues.phoneNumber,
        workerValues.businessName,
        workerValues.w_address,
      )
      .subscribe({
        next: () => {
          this.toastService.showToast(
            this.translate.instant(
              'SIGNUP-DIALOG.NEIGHBOR_SIGNUP_SUCCESSFUL_LOGGING_YOU_IN',
            ),
            'success',
          );
          // Attempt auto-login
          this.authService
            .login(workerValues.w_mail, workerValues.w_password)
            .subscribe({
              next: success => {
                this.loading = false;
                if (success) {
                  // Get the user's role from UserSessionService
                  const userRole = this.userSessionService.getCurrentRole();
                  switch (userRole) {
                    case Role.WORKER:
                      const workerUrl = this.linkStorage.getLink(
                        LinkKey.USER_WORKER,
                      );
                      this.router
                        .navigate(['services', 'profile', workerUrl])
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
                    this.translate.instant(
                      'SIGNUP-DIALOG.LOGIN_FAILED_AFTER_SIGNUP_PLEASE_TRY_MANUALLY',
                    ),
                    'error',
                  );
                  this.closeSignupDialog();
                }
              },
              error: err => {
                console.error('Automatic login error:', err);
                this.loading = false;
                this.toastService.showToast(
                  this.translate.instant(
                    'SIGNUP-DIALOG.LOGIN_FAILED_AFTER_SIGNUP_PLEASE_TRY_MANUALLY',
                  ),
                  'error',
                );
                this.closeSignupDialog();
              },
            });
        },
        error: error => {
          console.error('Error during neighbor signup:', error);
          this.loading = false;
          this.toastService.showToast(
            this.translate.instant(
              'SIGNUP-DIALOG.SIGNUP_FAILED_PLEASE_TRY_AGAIN',
            ),
            'error',
          );
        },
      });
  }

  // =======================
  // = Profession Multi-Select Logic (mirroring your Neighborhoods approach)
  // =======================

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
      next: response => {
        this.neighborhoodsList = response.neighborhoods;
      },
      error: error => {
        console.error('Error getting neighborhoods:', error);
        this.toastService.showToast(
          this.translate.instant(
            'SIGNUP-DIALOG.THERE_WAS_A_PROBLEM_FETCHING_THE_NEIGHBORHOODS_TRY',
          ),
          'error',
        );
      },
    });
  }

  getLanguages(): void {
    this.languageService.getLanguages().subscribe({
      next: languages => {
        this.languages = languages;
      },
      error: error => {
        console.error('Error getting languages:', error);
        this.toastService.showToast(
          this.translate.instant(
            'SIGNUP-DIALOG.COULD_NOT_RETRIEVE_AVAILABLE_LANGUAGES_TRY_AGAIN',
          ),
          'error',
        );
      },
    });
  }

  fetchNeighborhoods = (page: number, size: number): Observable<any> => {
    return this.neighborhoodService.getNeighborhoods({ page, size }).pipe(
      map(response => ({
        items: response.neighborhoods,
        currentPage: response.currentPage,
        totalPages: response.totalPages,
      })),
    );
  };

  displayNeighborhood = (neighborhood: Neighborhood): string => {
    return neighborhood.name;
  };

  fetchProfessions = (): Observable<any> => {
    return this.professionService.getProfessions().pipe(
      map((professions: Profession[]) => ({
        items: professions,
        currentPage: 1,
        totalPages: 1,
      })),
    );
  };

  displayProfession = (profession: Profession): string => {
    return profession.displayName;
  };
}
