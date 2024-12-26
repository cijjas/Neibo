import { Component } from '@angular/core';

@Component({
  selector: 'login',
  templateUrl: './login-page.component.html'
})
export class LoginPageComponent {
  showLoginDialog: boolean = false;
  showSignupDialog: boolean = false;

  openLoginDialog(): void {
    this.showLoginDialog = true;
  }

  openSignupDialog(): void {
    this.showSignupDialog = true;
  }
  closeLoginDialog(): void {
    this.showLoginDialog = false;
  }

}
