import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { ImageService } from '@core/index';

@Component({
  selector: 'app-service-providers-edit-dialog',
  templateUrl: './service-providers-edit-dialog.component.html',
})
export class ServiceProvidersEditDialogComponent implements OnChanges {
  @Input() editDialogVisible = false;
  @Input() worker: any; 
  @Output() closeDialog = new EventEmitter<void>();
  @Output() saveProfile = new EventEmitter<any>();

  businessName = '';
  bio = '';
  phoneNumber = '';
  address = '';

  existingBackgroundImage = '';

  imageFile: File | null = null;

  constructor(private imageService: ImageService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['worker'] && changes['worker'].currentValue) {
      const worker = changes['worker'].currentValue;
      this.businessName = worker.businessName || '';
      this.bio = worker.bio || '';
      this.phoneNumber = worker.phoneNumber || '';
      this.address = worker.address || '';

      this.existingBackgroundImage = worker.backgroundImage || '';
    }
  }

  closeEditDialog(): void {
    this.closeDialog.emit();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files?.length) {
      this.imageFile = input.files[0];
    }
  }

  submitEditProfileForm(): void {
    if (!this.imageFile) {
      const updatedWorkerDto = {
        businessName: this.businessName,
        bio: this.bio,
        phoneNumber: this.phoneNumber,
        address: this.address,
      };
      this.saveProfile.emit(updatedWorkerDto);
      this.closeEditDialog();
      return;
    }

    this.imageService.createImage(this.imageFile).subscribe({
      next: (uploadedImageUrl) => {
        const updatedWorkerDto = {
          businessName: this.businessName,
          bio: this.bio,
          phoneNumber: this.phoneNumber,
          address: this.address,
          backgroundImage: uploadedImageUrl, 
        };

        this.saveProfile.emit(updatedWorkerDto);
        this.closeEditDialog();
      },
      error: (err) => {
        console.error('Error uploading image', err);
      },
    });
  }
}
