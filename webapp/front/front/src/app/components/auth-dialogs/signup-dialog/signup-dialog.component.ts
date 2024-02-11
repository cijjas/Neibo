import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'signup-dialog',
  templateUrl: './signup-dialog.component.html'
})
export class SignupDialogComponent {
  @Input() showSignupDialog: boolean = false;
  @Output() showSignupDialogChange = new EventEmitter<boolean>();

  selectedOption: 'neighbor' | 'service' = 'neighbor';

  selectOption(option: 'neighbor' | 'service'): void {
    this.selectedOption = option;
  }

  closeSignupDialog(): void {
    this.showSignupDialog = false;
    this.showSignupDialogChange.emit(this.showSignupDialog);
  }
}
