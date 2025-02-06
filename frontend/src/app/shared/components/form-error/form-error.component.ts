import { Component, Input } from '@angular/core';
import { AbstractControl } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';

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

  constructor(private translate: TranslateService) {}

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
    const translationKey = `FORM-ERROR-COMPONENT.${errorKey}`;
    return (
      this.translate.instant(translationKey, errorValue) || 'Invalid field'
    );
  }
}
