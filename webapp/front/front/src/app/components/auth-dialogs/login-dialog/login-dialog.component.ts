import { ChangeDetectorRef, ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../shared/services/auth.service';
import { Router } from "@angular/router";

@Component({
  selector: 'login-dialog',
  templateUrl: './login-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginDialogComponent
  implements OnInit {
  @Input() showLoginDialog: boolean = false;
  @Input() showSignupDialog: boolean = false;
  @Output() showLoginDialogChange = new EventEmitter<boolean>();
  @Output() showSignupDialogChange = new EventEmitter<boolean>();

  loginForm: FormGroup;
  loginFailed: boolean = false;
  loading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef // Add this

  ) { }
  ngOnInit() {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
      rememberMe: new FormControl(false)
    });
  }



  closeLoginDialog(): void {
    this.showLoginDialog = false;
    this.showLoginDialogChange.emit(this.showLoginDialog);
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
      this.authService.login(email, password, rememberMe)
        .subscribe({
          next: (success) => {
            this.loading = false;
            if (success) {
              this.router.navigate(['/feed']).then(() => {
                this.closeLoginDialog();
              });
            } else {
              this.loginFailed = true;
              this.loading = false;
              this.clearPasswordField(); // Clear the password field
              this.triggerViewUpdate();
            }
          },
          error: (error) => {
            this.loginFailed = true;
            this.loading = false;
            this.clearPasswordField(); // Clear the password field
            console.error('Login failed:', error);
            this.triggerViewUpdate();
          }
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
