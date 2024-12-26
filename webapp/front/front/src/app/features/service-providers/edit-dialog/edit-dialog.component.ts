import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-edit-dialog',
  templateUrl: './edit-dialog.component.html',
})
export class EditDialogComponent {
  @Input() editDialogVisible = false;
  @Input() worker: any;
  @Output() closeDialog = new EventEmitter<void>();
  @Output() saveProfile = new EventEmitter<any>();

  businessName = '';
  bio = '';
  phoneNumber = '';
  address = '';

  ngOnInit(): void {
    if (this.worker) {
      this.businessName = this.worker.businessName || '';
      this.bio = this.worker.bio || '';
      this.phoneNumber = this.worker.phoneNumber || '';
      this.address = this.worker.address || '';
    }
  }

  closeEditDialog(): void {
    this.closeDialog.emit();
  }

  submitEditProfileForm(): void {
    this.saveProfile.emit({
      ...this.worker,
      businessName: this.businessName,
      bio: this.bio,
      phoneNumber: this.phoneNumber,
      address: this.address,
    });
    this.closeEditDialog();
  }
}
