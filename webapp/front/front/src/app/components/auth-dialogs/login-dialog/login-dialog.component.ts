import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'login-dialog',
  templateUrl: './login-dialog.component.html'
})
export class LoginDialogComponent {
  @Input() showLoginDialog: boolean = false;
  @Input() showSignupDialog: boolean = false;
  @Output() showLoginDialogChange = new EventEmitter<boolean>();
  @Output() showSignupDialogChange = new EventEmitter<boolean>();

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
}
