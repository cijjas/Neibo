import { Component, Input, OnInit } from '@angular/core';
import {
  ConfirmationService,
  HateoasLinksService,
  ToastService,
  UserSessionService,
} from '@core/index';
import { ActivatedRoute, Router } from '@angular/router';
import { Event, EventService, LinkKey, Role } from '@shared/index';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-calendar-events-column',
  templateUrl: './calendar-events-column.component.html',
})
export class CalendarEventsColumnComponent implements OnInit {
  @Input() selectedDate: Date;
  events: Event[] = [];
  isLoading = true;
  placeholders = [1, 2, 3];
  isAdmin = false;
  currentPage: number = 1;
  totalPages: number = 1;
  pageSize: number = 5; // Adjust the number of events per page

  constructor(
    private eventService: EventService,
    private linkStorage: HateoasLinksService,
    private route: ActivatedRoute,
    private router: Router,
    private userSessionService: UserSessionService,
    private confirmationService: ConfirmationService,
    private translate: TranslateService,
    private toastService: ToastService,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const dateParam = params['date'];
      this.currentPage = params['page'] ? parseInt(params['page'], 10) : 1;

      this.selectedDate = dateParam ? new Date(dateParam) : new Date();
      this.loadEventsForSelectedDate();
    });

    this.isAdmin =
      this.userSessionService.getCurrentRole() == Role.ADMINISTRATOR;
  }

  loadEventsForSelectedDate(): void {
    if (!this.selectedDate) {
      console.error('No selected date provided');
      return;
    }

    const eventUrl = this.linkStorage.getLink(LinkKey.NEIGHBORHOOD_EVENTS);
    const dateString = this.selectedDate.toISOString().split('T')[0];

    // Clear events before loading
    this.events = [];
    this.isLoading = true;

    this.eventService
      .getEvents(eventUrl, {
        forDate: dateString,
        page: this.currentPage,
        size: this.pageSize,
      })
      .subscribe({
        next: response => {
          this.events = response.events;
          this.totalPages = response.totalPages;

          // If no events are returned, set totalPages to 1
          if (this.events.length === 0) {
            this.totalPages = 1;
          }
        },
        error: error => console.error('Error fetching events:', error),
        complete: () => (this.isLoading = false),
      });
  }

  onPageChange(newPage: number): void {
    this.currentPage = newPage;
    this.router.navigate([], {
      queryParams: {
        page: this.currentPage,
        date: this.selectedDate.toISOString().split('T')[0],
      },
      queryParamsHandling: 'merge',
    });
    this.loadEventsForSelectedDate();
  }

  deleteEvent(event: Event): void {
    this.confirmationService
      .askForConfirmation({
        title: this.translate.instant(
          'CALENDAR-EVENTS-COLUMN.DELETE_EVENT_TITLE',
        ),
        message: this.translate.instant(
          'CALENDAR-EVENTS-COLUMN.ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_EVENT',
          { eventName: event.name },
        ),
        confirmText: this.translate.instant(
          'CALENDAR-EVENTS-COLUMN.YES_DELETE',
        ),
        cancelText: this.translate.instant('CALENDAR-EVENTS-COLUMN.CANCEL'),
      })
      .subscribe(confirmed => {
        if (confirmed) {
          this.eventService.deleteEvent(event.self).subscribe({
            next: () => {
              this.toastService.showToast(
                this.translate.instant(
                  'CALENDAR-EVENTS-COLUMN.EVENT_DELETED_SUCCESSFULLY',
                  { eventName: event.name },
                ),
                'success',
              );
              this.loadEventsForSelectedDate();
            },
            error: err => {
              console.error(
                this.translate.instant(
                  'CALENDAR-EVENTS-COLUMN.ERROR_DELETING_EVENT',
                  { eventName: event.name },
                ),
                err,
              );
              this.toastService.showToast(
                this.translate.instant(
                  'CALENDAR-EVENTS-COLUMN.FAILED_TO_DELETE_EVENT',
                  { eventName: event.name },
                ),
                'error',
              );
            },
          });
        }
      });
  }
}
