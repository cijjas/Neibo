import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { ImageService } from '@core/index';
import { VALIDATION_CONFIG } from '@shared/constants/validation-config'; // adjust this import

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
  imagePreviewUrl: string | ArrayBuffer | null = null;

  // To store validation errors from imageValidator
  imageValidationErrors: { fileSize?: any; fileFormat?: any } | null = null;

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
    if (!input?.files?.length) {
      return;
    }

    this.imageFile = input.files[0];

    // --- Validate the image using VALIDATION_CONFIG ---
    const validatorFn = VALIDATION_CONFIG.imageValidator;
    const controlMock = { value: this.imageFile } as any;
    const errors = validatorFn(controlMock);
    this.imageValidationErrors = errors;

    // If validation passes, create a preview
    if (!errors) {
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreviewUrl = reader.result;
      };
      reader.readAsDataURL(this.imageFile);
    } else {
      // If invalid, clear preview
      this.imagePreviewUrl = null;
    }
  }

  removeImage(imageInput: HTMLInputElement): void {
    // Clear image data
    this.imageFile = null;
    this.imagePreviewUrl = null;
    this.existingBackgroundImage = '';
    this.imageValidationErrors = null;

    // Also clear the file input
    imageInput.value = '';
  }

  submitEditProfileForm(): void {
    // If user didn’t select a new file or has removed the existing image,
    // but existingBackgroundImage is empty, we proceed with no backgroundImage
    if (!this.imageFile) {
      const updatedWorkerDto: any = {
        businessName: this.businessName,
        bio: this.bio,
        phoneNumber: this.phoneNumber,
        address: this.address,
      };

      // If the user did not remove the existing background image,
      // keep it. If they've removed it (existingBackgroundImage = ''),
      // do not send it.
      if (this.existingBackgroundImage) {
        updatedWorkerDto.backgroundImage = this.existingBackgroundImage;
      }

      this.saveProfile.emit(updatedWorkerDto);
      this.closeEditDialog();
      return;
    }

    // If there are validation errors, do not proceed
    if (this.imageValidationErrors) {
      return;
    }

    // Otherwise, upload the new file
    this.imageService.createImage(this.imageFile).subscribe({
      next: uploadedImageUrl => {
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
      error: err => {
        console.error('Error uploading image', err);
      },
    });
  }
}
