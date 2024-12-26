import { Component, Input, OnInit } from '@angular/core';
import { HateoasLinksService } from '@core/index';
import { ActivatedRoute, Router } from '@angular/router';
import { Event, EventService } from '@shared/index';

@Component({
  selector: 'app-calendar-events-column',
  templateUrl: './calendar-events-column.component.html',
})
export class CalendarEventsColumnComponent implements OnInit {
  @Input() selectedDate: Date;
  events: Event[] = [];
  isLoading = true;
  placeholders = [1, 2, 3];
  isAdmin = false; // Update based on your authentication logic
  // Pagination properties
  currentPage: number = 1;
  totalPages: number = 1;
  pageSize: number = 5; // Adjust the number of events per page

  constructor(
    private eventService: EventService,
    private linkStorage: HateoasLinksService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const dateParam = params['date'];
      this.currentPage = params['page'] ? parseInt(params['page'], 10) : 1;

      this.selectedDate = dateParam ? new Date(dateParam) : new Date();
      this.loadEventsForSelectedDate();
    });
  }

  loadEventsForSelectedDate(): void {
    if (!this.selectedDate) {
      console.error('No selected date provided');
      return;
    }

    const eventUrl = this.linkStorage.getLink('neighborhood:events');
    const dateString = this.selectedDate.toISOString().split('T')[0];

    this.isLoading = true;

    this.eventService
      .getEvents(eventUrl,
        {
          forDate: dateString,
          page: this.currentPage,
          size: this.pageSize
        }
      )
      .subscribe({
        next: (response) => {
          this.events = response.events;
          this.totalPages = response.totalPages;
        },
        error: (error) => console.error('Error fetching events:', error),
        complete: () => (this.isLoading = false),
      });
  }

  onPageChange(newPage: number): void {
    this.currentPage = newPage;
    this.router.navigate([], {
      queryParams: { page: this.currentPage, date: this.selectedDate.toISOString().split('T')[0] },
      queryParamsHandling: 'merge',
    });
    this.loadEventsForSelectedDate();
  }
}
