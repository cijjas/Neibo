import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

// Import your services and models as needed
import {
  AttendanceService,
  EventService,
  Attendance,
  Event,
  LinkKey,
} from '@shared/index';
import { HateoasLinksService, ToastService } from '@core/index';
import { AppTitleKeys } from '@shared/constants/app-titles';
import { Title } from '@angular/platform-browser';
@Component({
  selector: 'app-calendar-event-page',
  templateUrl: './calendar-event-page.component.html',
})
export class CalendarEventPageComponent implements OnInit {
  // Event details
  event: Event;
  willAttend = false;

  // Attendees
  attendees: Attendance[] = []; // Replace `Attendance` with your actual Attendee type if different

  // Attendee pagination
  attendanceCurrentPage = 1;
  attendancePageSize = 20;
  attendanceTotalPages = 1;

  // Example constructor for injecting your needed services
  constructor(
    private route: ActivatedRoute,
    private linkService: HateoasLinksService,
    private eventService: EventService,
    private attendanceService: AttendanceService,
    private toastService: ToastService,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe(({ event }) => {
      if (!event) {
        console.error('Event not found or failed to resolve');
        return;
      }
      this.event = event;

      this.translate
        .get(AppTitleKeys.CALENDAR_EVENT_PAGE, {
          eventName: event.name,
        })
        .subscribe((translatedTitle: string) => {
          this.titleService.setTitle(translatedTitle);
        });

      // Load attendees after event is available
      this.loadAttendance();

      // Check if user will attend
      this.checkUserAttendance();
    });

    // 2) Watch query params for "attendancePage"
    this.route.queryParamMap.subscribe(params => {
      const attendancePageParam = params.get('attendancePage');
      this.attendanceCurrentPage = attendancePageParam
        ? +attendancePageParam
        : 1;
      this.loadAttendance(); // fetch attendees again if page changes
    });
  }

  checkUserAttendance(): void {
    if (!this.event) return;

    this.attendanceService
      .getAttendances({
        forUser: this.linkService.getLink(LinkKey.USER_SELF),
        forEvent: this.event.self,
        page: 1,
        size: 1,
      })
      .subscribe({
        next: next => {
          this.willAttend = next.attendances.length !== 0;
        },
        error: () => {
          console.error('Error getting attendance');
        },
      });
  }

  // Example: fetch the event from your EventService
  loadEvent(eventId: string): void {
    this.eventService.getEvent(eventId).subscribe(event => {
      this.event = event;

      this.loadAttendance();
    });
  }

  loadAttendance(): void {
    this.attendees = [];
    this.attendanceService
      .getAttendances({
        forEvent: this.event.self,
        page: this.attendanceCurrentPage,
        size: this.attendancePageSize,
      })
      .subscribe(result => {
        this.attendees = result.attendances ?? [];
        this.attendanceTotalPages = result.totalPages;
        this.attendanceCurrentPage = result.currentPage;
      });
  }

  attendEvent(): void {
    this.attendanceService.createAttendance(this.event.self).subscribe(() => {
      this.willAttend = true;
      // Manually increment the count in your local event object
      this.event.attendeesCount = (this.event.attendeesCount ?? 0) + 1;

      this.toastService.showToast(
        this.translate.instant(
          'CALENDAR-EVENT-PAGE.ATTENDANCE_TO_EVENT_CONFIRMED',
        ),
        'success',
      );
      this.loadAttendance(); // Refresh the list of attendees
    });
  }

  unattendEvent(): void {
    this.attendanceService.deleteAttendance(this.event.self).subscribe(() => {
      this.willAttend = false;
      // Manually decrement the count
      this.event.attendeesCount = Math.max(
        (this.event.attendeesCount ?? 1) - 1,
        0,
      );

      this.toastService.showToast(
        this.translate.instant(
          'CALENDAR-EVENT-PAGE.YOU_WERE_UNLISTED_FROM_THE_EVENT',
        ),
        'success',
      );
      this.loadAttendance(); // Refresh the list of attendees
    });
  }

  onAttendancePageChange(page: number): void {
    this.attendanceCurrentPage = page;
    this.loadAttendance(); // Reload the attendees for the new page
  }
}
