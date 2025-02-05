import { AbstractControl, FormGroup, ValidationErrors } from '@angular/forms';

/**
 * Validator for ensuring that at least one tag is selected.
 */
export function atLeastOneTagSelected(
  control: AbstractControl,
): ValidationErrors | null {
  const tags = control.value || [];
  return tags.length > 0 ? null : { noTagsSelected: true };
}

/**
 * Factory function for validating an image file’s size and format.
 * Returns error objects with keys only.
 */
export function imageSizeAndFormatValidator(
  maxSizeMB: number = 2,
  allowedFormats: string[] = ['image/jpeg', 'image/png', 'image/gif'],
) {
  return (control: AbstractControl): ValidationErrors | null => {
    const file = control.value as File;
    if (!file) {
      return null; // No file provided, so no validation error.
    }

    const maxSizeBytes = maxSizeMB * 1024 * 1024;
    if (file.size > maxSizeBytes) {
      // Return the maxSizeMB for use in error messages if desired.
      return { fileSize: { requiredMax: maxSizeMB } };
    }

    if (!allowedFormats.includes(file.type)) {
      return { fileFormat: true };
    }

    return null;
  };
}

// validation-config.ts
export const VALIDATION_CONFIG = {
  // Amenity create
  name: {
    maxLength: 50,
    required: true,
  },
  description: {
    maxLength: 200,
    required: true,
  },

  // Announcements
  subject: {
    required: true,
    maxLength: 100,
  },
  message: {
    required: true,
    maxLength: 500,
  },

  // General

  atLeastOneTagSelected,
  imageValidator: imageSizeAndFormatValidator(2, [
    'image/jpeg',
    'image/png',
    'image/gif',
  ]),

  startBeforeEndValidator: (
    control: AbstractControl,
  ): ValidationErrors | null => {
    const group = control as FormGroup;
    const start = group.get('startTime')?.value;
    const end = group.get('endTime')?.value;
    if (!start || !end) {
      return null;
    }
    return start >= end ? { startBeforeEnd: true } : null;
  },

  PATTERN_ALPHA_NUM_SPACE_HYPHEN: /^[a-zA-Z0-9\s\-]*$/,
};
