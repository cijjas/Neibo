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
import { TranslateService } from '@ngx-translate/core';
import { VALIDATION_CONFIG } from '@shared/constants/validation-config';
import { AppTitleKeys } from '@shared/constants/app-titles';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-admin-create-event',
  templateUrl: './admin-create-event-page.component.html',
})
export class AdminCreateEventPageComponent {
  eventForm: FormGroup;
  todayString: string = '';

  constructor(
    private fb: FormBuilder,
    private eventService: EventService,
    private toastService: ToastService,
    private calendarService: CalendarService,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit() {
    const title = this.translate.instant(AppTitleKeys.ADMIN_CREATE_EVENT_PAGE);
    this.titleService.setTitle(title);

    this.todayString = new Date().toISOString().split('T')[0];
    this.eventForm = this.fb.group(
      {
        name: ['', [Validators.required]],
        description: ['', [Validators.required]],
        date: ['', [Validators.required]],
        startTime: ['', [Validators.required]],
        endTime: ['', [Validators.required]],
      },
      {
        validators: [VALIDATION_CONFIG.startBeforeEndValidator],
      },
    );
  }

  onSubmit() {
    if (this.eventForm.invalid) {
      this.eventForm.markAllAsTouched();
      return;
    }

    const formValue = { ...this.eventForm.value };

    // Convert date string to Date object before calling the service
    const eventDate = new Date(formValue.date);

    // Ensure startTime and endTime include seconds
    formValue.startTime = this.ensureSeconds(formValue.startTime);
    formValue.endTime = this.ensureSeconds(formValue.endTime);

    this.eventService
      .createEvent(
        formValue.name,
        formValue.description,
        eventDate,
        formValue.startTime,
        formValue.endTime,
      )
      .subscribe({
        next: () => {
          this.toastService.showToast(
            this.translate.instant(
              'ADMIN-CREATE-EVENT.EVENT_CREATED_SUCCESSFULLY',
            ),
            'success',
          );

          // Reset the form
          this.eventForm.reset();
          this.eventForm.markAsPristine();

          // Notify or reload the calendar widget
          this.calendarService.triggerReload();
        },
        error: () => {
          this.toastService.showToast(
            this.translate.instant('ADMIN-CREATE-EVENT.FAILED_CREATING_EVENT'),
            'error',
          );
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
