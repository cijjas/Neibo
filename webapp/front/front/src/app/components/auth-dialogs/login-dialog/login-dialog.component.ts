import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {  FormControl ,FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../shared/services/auth.service';

@Component({
  selector: 'login-dialog',
  templateUrl: './login-dialog.component.html'
})
export class LoginDialogComponent implements OnInit {
  @Input() showLoginDialog: boolean = false;
  @Input() showSignupDialog: boolean = false;
  @Output() showLoginDialogChange = new EventEmitter<boolean>();
  @Output() showSignupDialogChange = new EventEmitter<boolean>();

  loginForm: FormGroup;
  loginFailed: boolean = false;
  loading: boolean = false;

  constructor(private authService: AuthService) {}
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
        .subscribe((success) => {
          this.loading = false;
          if (success) {
            this.closeLoginDialog();
          } else {
            this.loginForm.setErrors(null);
            this.loginForm.markAsPristine();
            this.loginForm.markAsUntouched();
            this.loginFailed = true;
          }
        });
    } else { // caso de mala intención
      this.loginForm.markAllAsTouched();
      this.loading = false;
    }
  }



}
