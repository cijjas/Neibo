import { Component } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { ToastService } from '@core/index';
import { CalendarService, EventService } from '@shared/index';

@Component({
  selector: 'app-admin-create-event',
  templateUrl: './admin-create-event.component.html',
})
export class AdminCreateEventComponent {
  eventForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private eventService: EventService,
    private toastService: ToastService,
    private calendarService: CalendarService
  ) {
    // Inline definition of the validator function
    const startBeforeEndValidator: ValidatorFn = (
      control: AbstractControl
    ): ValidationErrors | null => {
      const group = control as FormGroup;
      const start = group.get('startTime')?.value;
      const end = group.get('endTime')?.value;
      if (!start || !end) return null;
      return start >= end ? { startBeforeEnd: true } : null;
    };

    // Now define the form and pass the validator
    this.eventForm = this.fb.group(
      {
        name: ['', [Validators.required]],
        description: ['', [Validators.required]],
        date: ['', [Validators.required]],
        startTime: ['', [Validators.required]],
        endTime: ['', [Validators.required]],
      },
      {
        validators: [startBeforeEndValidator],
      }
    );
  }

  onSubmit() {
    if (this.eventForm.invalid) {
      this.eventForm.markAllAsTouched();
      return;
    }

    const formValue = { ...this.eventForm.value };

    // Ensure startTime and endTime include seconds
    formValue.startTime = this.ensureSeconds(formValue.startTime);
    formValue.endTime = this.ensureSeconds(formValue.endTime);

    this.eventService
      .createEvent(
        formValue.name,
        formValue.description,
        formValue.date,
        formValue.startTime,
        formValue.endTime
      )
      .subscribe({
        next: () => {
          // Show success toast
          this.toastService.showToast('Event created successfully!', 'success');

          // Reset the form
          this.eventForm.reset();
          this.eventForm.markAsPristine();

          // Notify or reload the calendar widget
          this.calendarService.triggerReload();
        },
        error: () => {
          this.toastService.showToast('Failed creating event.', 'error');
        },
      });
  }

  private ensureSeconds(time: string): string {
    if (!time.includes(':')) return time; // Handle edge case if input is malformed
    const parts = time.split(':');
    if (parts.length === 2) {
      return `${time}:00`; // Add seconds if missing
    }
    return time; // Return as-is if already in 'HH:mm:ss'
  }
}
