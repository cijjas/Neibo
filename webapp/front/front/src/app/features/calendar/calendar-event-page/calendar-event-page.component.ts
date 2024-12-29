import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

// Import your services and models as needed
import { AttendanceService, EventService, Attendance, Event } from '@shared/index';
import { HateoasLinksService } from '@core/index';
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
    private attendanceService: AttendanceService
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
  }

  // Example: fetch the event from your EventService
  loadEvent(eventId: string): void {
    this.eventService.getEvent(eventId).subscribe(event => {
      this.event = event;

      this.loadAttendance();
    });
  }

  loadAttendance(): void {
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

  get formattedDate(): string {
    if (!this.event) return '';
    const dateObj = new Date(this.event.eventDate);
    return `${dateObj.getDate()}-${(dateObj.getMonth() + 1)}-${dateObj.getFullYear()} `;
  }

  attendEvent(): void {
    const userUrl = this.linkService.getLink('user:self');
    this.attendanceService.createAttendance(this.event.self, userUrl)
      .subscribe(() => {
        this.willAttend = true;
        // Reload attendees if needed
        this.loadAttendance();
      });
  }

  unattendEvent(): void {
    this.attendanceService.deleteAttendance(

    )
      .subscribe(() => {
        this.willAttend = false;
        // Reload attendees
        this.loadAttendance();
      });
  }

  onAttendancePageChange(page: number): void {
    this.attendanceCurrentPage = page;
    this.loadAttendance(); // Reload the attendees for the new page
  }

}
