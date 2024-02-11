import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AuthService} from "../../../shared/services/auth.service";

@Component({
  selector: 'login-dialog',
  templateUrl: './login-dialog.component.html'
})
export class LoginDialogComponent {
  @Input() showLoginDialog: boolean = false;
  @Input() showSignupDialog: boolean = false;
  @Output() showLoginDialogChange = new EventEmitter<boolean>();
  @Output() showSignupDialogChange = new EventEmitter<boolean>();
  email: string = '';
  password: string = '';
  rememberMe: boolean = false;

  constructor(private authService: AuthService) {}


  closeLoginDialog(): void {
    this.showLoginDialog = false;
    this.showLoginDialogChange.emit(this.showLoginDialog);
  }

  /*Open signup from "signup now"*/
  openSignupDialog(): void {
    this.showLoginDialog = false;
    this.showLoginDialogChange.emit(false);
    this.showSignupDialog = true;
    this.showSignupDialogChange.emit(true);
  }

  tryLogin(): void {
    console.log('Trying to login with email: ' + this.email + ' and password: ' + this.password);
    if (this.email && this.password) {
      this.authService.login(this.email, this.password);
      this.closeLoginDialog();
    }
  }
}
