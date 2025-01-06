import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

// Import your services and models as needed
import { AttendanceService, EventService, Attendance, Event, LinkKey } from '@shared/index';
import { HateoasLinksService, ToastService } from '@core/index';
@Component({
  selector: 'app-calendar-event-page',
  templateUrl: './calendar-event-page.component.html',
})
export class CalendarEventPageComponent implements OnInit {
  // Appearance
  darkMode = false;

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
  ) { }

  ngOnInit(): void {
    // 1) Get the Event ID from the URL (/events/:id)
    const eventId = this.route.snapshot.paramMap.get('id');
    if (eventId) {
      // Build the attendance URL.
      // For example, the backend might expect /events/1/attendance
      this.loadEvent(eventId);
    }

    // 2) Watch query params for "attendancePage"
    this.route.queryParamMap.subscribe(params => {
      const attendancePageParam = params.get('attendancePage');
      this.attendanceCurrentPage = attendancePageParam ? +attendancePageParam : 1;
      this.loadAttendance(); // fetch attendees again if page changes
    });

    this.attendanceService.getAttendances({ forUser: this.linkService.getLink(LinkKey.USER_SELF), forEvent: eventId, page: 1, size: 1 }).subscribe({
      next: (next) => {
        this.willAttend = next.attendances.length != 0;
      },
      error: (error) => {
        console.error("Error getting attendance");
      }
    })
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
        size: this.attendancePageSize
      })
      .subscribe(result => {
        this.attendees = result.attendances ?? [];
        this.attendanceTotalPages = result.totalPages;
        this.attendanceCurrentPage = result.currentPage;
      });
  }



  attendEvent(): void {
    this.attendanceService.createAttendance(this.event.self)
      .subscribe(() => {
        this.willAttend = true;
        // Manually increment the count in your local event object
        this.event.attendeesCount = (this.event.attendeesCount ?? 0) + 1;

        this.toastService.showToast('Attendance to event confirmed.', 'success');
        this.loadAttendance();  // Refresh the list of attendees
      });
  }

  unattendEvent(): void {
    this.attendanceService.deleteAttendance(this.event.self)
      .subscribe(() => {
        this.willAttend = false;
        // Manually decrement the count
        this.event.attendeesCount = Math.max((this.event.attendeesCount ?? 1) - 1, 0);

        this.toastService.showToast('You were unlisted from the event.', 'success');
        this.loadAttendance();  // Refresh the list of attendees
      });
  }


  onAttendancePageChange(page: number): void {
    this.attendanceCurrentPage = page;
    this.loadAttendance(); // Reload the attendees for the new page
  }

}
