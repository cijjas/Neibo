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
import { LinkKey, Roles } from '@shared/index';

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
    private cdr: ChangeDetectorRef
  ) { }
  ngOnInit() {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
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
    if (this.authService.isLoggedIn()) {
      this.closeLoginDialog(); // Close if already logged in
      return;
    }
    this.loading = true;

    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;

      this.authService.login(email, password).subscribe({
        next: (success) => {
          this.loading = false;

          if (success) {
            // Get the user's role from UserSessionService
            const userRole = this.authService.getCurrentRole();

            switch (userRole) {
              case Roles.WORKER:
                const workerUrl = this.linkStorage.getLink(LinkKey.USER_WORKER);
                this.router
                  .navigate(['services', 'profile', workerUrl])
                  .then(() => this.closeLoginDialog());
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
                  .then(() => this.closeLoginDialog());
                break;

              case Roles.UNVERIFIED_NEIGHBOR:
                this.router
                  .navigate(['unverified'])
                  .then(() => this.closeLoginDialog());

                break;
              case Roles.REJECTED:
                this.router
                  .navigate(['rejected'])
                  .then(() => this.closeLoginDialog());
                break;

              default:
                // Fallback for unexpected or null roles
                this.router
                  .navigate(['not-found'])
                  .then(() => this.closeLoginDialog());
                break;
            }
          } else {
            this.handleLoginFailure();
          }
        },
        error: (error) => {
          console.error('Login failed:', error);
          this.handleLoginFailure();
        },
      });
    } else {
      this.loginForm.markAllAsTouched();
      this.loading = false;
      this.triggerViewUpdate();
    }
  }

  private handleLoginFailure(): void {
    this.loginFailed = true;
    this.loading = false;
    this.clearPasswordField();
    this.triggerViewUpdate();
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
