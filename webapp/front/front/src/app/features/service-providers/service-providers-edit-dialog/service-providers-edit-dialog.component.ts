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
  @Input() worker: any; // The fully mapped Worker from parent component
  @Output() closeDialog = new EventEmitter<void>();
  @Output() saveProfile = new EventEmitter<any>();

  businessName = '';
  bio = '';
  phoneNumber = '';
  address = '';

  /**
   * Keep track of the existing background image link
   * (from worker.backgroundImage).
   */
  existingBackgroundImage = '';

  /**
   * For file upload
   */
  imageFile: File | null = null;

  constructor(private imageService: ImageService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['worker'] && changes['worker'].currentValue) {
      const worker = changes['worker'].currentValue;
      this.businessName = worker.businessName || '';
      this.bio = worker.bio || '';
      this.phoneNumber = worker.phoneNumber || '';
      this.address = worker.address || '';

      // Save the existing background image link (if any)
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
    // If no new image is selected, just emit the data directly
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

    // Otherwise, upload the image first to get the URL
    this.imageService.createImage(this.imageFile).subscribe({
      next: (uploadedImageUrl) => {
        console.log(uploadedImageUrl);
        // Construct the WorkerDto
        const updatedWorkerDto = {
          businessName: this.businessName,
          bio: this.bio,
          phoneNumber: this.phoneNumber,
          address: this.address,
          backgroundImage: uploadedImageUrl, // <--- set the new image URL
        };
        // Emit it to the parent
        this.saveProfile.emit(updatedWorkerDto);
        this.closeEditDialog();
      },
      error: (err) => {
        console.error('Error uploading image', err);
        // Optionally handle errors in the UI
      },
    });
  }
}
