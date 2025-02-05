import { Component, Input } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-form-error',
  template: `
    <div
      *ngIf="shouldShowErrors()"
      [ngClass]="{ 'text-center': centered, 'text-start': !centered }"
    >
      <small class="text-danger" *ngFor="let error of listOfErrors()">
        {{ getErrorMessage(error) }}
      </small>
    </div>
  `,
  styles: [
    `
      .text-center {
        text-align: center;
      }
      .text-start {
        text-align: start;
      }
    `,
  ],
})
export class FormErrorComponent {
  @Input() control!: AbstractControl;
  @Input() centered: boolean = false;
  @Input() errorMessages: { [key: string]: (errorValue?: any) => string } = {
    required: () => 'This field is required',
    maxlength: (err: any) =>
      `The value is too long (max ${err.requiredLength} characters)`,
    pattern: () => 'Invalid format',
    // For image validator errors:
    fileSize: (err: any) =>
      `File size should be less than ${err.requiredMax} MB.`,
    fileFormat: () =>
      'Invalid file format. Only JPEG, PNG, or GIF images are allowed.',
    // For tag selection:
    noTagsSelected: () => 'Please select at least one tag.',
    startBeforeEnd: () => 'Start time should be before end time.',
  };

  shouldShowErrors(): boolean {
    return (
      this.control &&
      this.control.errors &&
      (this.control.touched || this.control.dirty)
    );
  }

  listOfErrors(): string[] {
    return this.control ? Object.keys(this.control.errors || {}) : [];
  }

  getErrorMessage(errorKey: string): string {
    const errorValue = this.control.errors
      ? this.control.errors[errorKey]
      : null;
    const messageFunc = this.errorMessages[errorKey];
    return messageFunc ? messageFunc(errorValue) : 'Invalid field';
  }
}
