import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-service-providers-edit-dialog',
  templateUrl: './service-providers-edit-dialog.component.html',
})
export class ServiceProvidersEditDialogComponent implements OnChanges {
  @Input() editDialogVisible = false;
  @Input() worker: any; // Worker data passed from parent component
  @Output() closeDialog = new EventEmitter<void>();
  @Output() saveProfile = new EventEmitter<any>();

  businessName = '';
  bio = '';
  phoneNumber = '';
  address = '';

  ngOnChanges(changes: SimpleChanges): void {
    // Check if the `worker` input changes
    if (changes['worker'] && changes['worker'].currentValue) {
      const worker = changes['worker'].currentValue;
      this.businessName = worker.businessName || '';
      this.bio = worker.bio || '';
      this.phoneNumber = worker.phoneNumber || '';
      this.address = worker.address || '';
    }
  }

  closeEditDialog(): void {
    this.closeDialog.emit();
  }

  submitEditProfileForm(): void {
    // Emit the updated profile details
    this.saveProfile.emit({
      businessName: this.businessName,
      bio: this.bio,
      phoneNumber: this.phoneNumber,
      address: this.address,
    });
    this.closeEditDialog();
  }
}