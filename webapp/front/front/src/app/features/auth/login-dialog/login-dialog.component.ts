import {
  ChangeDetectorRef,
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import {
  HateoasLinksService,
  AuthService,
  UserSessionService,
} from '@core/index';
import { LinkKey } from '@shared/index';

@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginDialogComponent implements OnInit {
  @Input() showLoginDialog: boolean = false;
  @Input() showSignupDialog: boolean = false;
  @Output() showLoginDialogChange = new EventEmitter<boolean>();
  @Output() showSignupDialogChange = new EventEmitter<boolean>();

  loginForm: FormGroup;
  showPassword = false;
  loginFailed: boolean = false;
  loading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private linkStorage: HateoasLinksService,
    private userSessionService: UserSessionService,
    private cdr: ChangeDetectorRef // Add this
  ) {}
  ngOnInit() {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
      rememberMe: new FormControl(false),
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  closeLoginDialog(): void {
    this.showLoginDialog = false;
    this.showLoginDialogChange.emit(this.showLoginDialog);
  }

  closeSignupDialog(): void {
    this.showSignupDialog = false;
    this.showSignupDialogChange.emit(this.showSignupDialog);
  }

  openSignupDialog(): void {
    this.showLoginDialog = false;
    this.showLoginDialogChange.emit(false);
    this.showSignupDialog = true;
    this.showSignupDialogChange.emit(true);
  }

  tryLogin(): void {
    this.loading = true;
    if (this.loginForm.valid) {
      const { email, password, rememberMe } = this.loginForm.value;

      this.authService.login(email, password, rememberMe).subscribe({
        next: (success) => {
          this.loading = false;
          if (success) {
            let currentUserRoleUrl: string = this.linkStorage.getLink(
              LinkKey.USER_USER_ROLE
            );
            let neighborUserRole: string = this.linkStorage.getLink(
              LinkKey.NEIGHBOR_USER_ROLE
            );
            let workerUserRole: string = this.linkStorage.getLink(
              LinkKey.WORKER_USER_ROLE
            );
            let unverifiedUserRole: string = this.linkStorage.getLink(
              LinkKey.UNVERIFIED_NEIGHBOR_USER_ROLE
            );
            let rejectedUserRole: string = this.linkStorage.getLink(
              LinkKey.REJECTED_USER_ROLE
            );
            let administratorUserRole: string = this.linkStorage.getLink(
              LinkKey.ADMINISTRATOR_USER_ROLE
            );

            if (currentUserRoleUrl === workerUserRole) {
              const workerUrl = this.linkStorage.getLink(LinkKey.USER_WORKER); // Fetch the workerUrl value
              this.router
                .navigate(['services', 'profile', workerUrl])
                .then(() => {
                  this.closeLoginDialog();
                });
            } else if (
              currentUserRoleUrl === neighborUserRole ||
              currentUserRoleUrl === administratorUserRole
            ) {
              const feedChannelUrl = this.linkStorage.getLink(
                LinkKey.NEIGHBORHOOD_FEED_CHANNEL
              );
              const nonePostStatus = this.linkStorage.getLink(
                LinkKey.NONE_POST_STATUS
              );
              this.router
                .navigate(['/posts'], {
                  queryParams: {
                    inChannel: feedChannelUrl,
                    withStatus: nonePostStatus,
                  },
                })
                .then(() => {
                  this.closeLoginDialog();
                });
            } else if (currentUserRoleUrl === unverifiedUserRole) {
              this.router.navigate(['/unverified']).then(() => {
                this.closeLoginDialog();
              });
            } else if (currentUserRoleUrl === rejectedUserRole) {
              this.router.navigate(['/unverified']).then(() => {
                this.closeLoginDialog();
              });
            }
          } else {
            this.loginFailed = true;
            this.loading = false;
            this.clearPasswordField();
            this.triggerViewUpdate();
          }
        },
        error: (error) => {
          this.loginFailed = true;
          this.loading = false;
          this.clearPasswordField();
          console.error('Login failed:', error);
          this.triggerViewUpdate();
        },
      });
    } else {
      this.loginForm.markAllAsTouched();
      this.loading = false;
      this.triggerViewUpdate();
    }
  }

  clearPasswordField(): void {
    this.loginForm.get('password')?.reset(); // Reset the password field
  }

  triggerViewUpdate(): void {
    this.cdr.detectChanges(); // Explicitly trigger Angular to update the UI
  }

  resetFormErrors(): void {
    this.loginForm.setErrors(null);
    this.loginForm.markAsDirty(); // Mark the form as dirty to show errors immediately
  }
}
