import { Component, OnInit, Input } from '@angular/core';
import { EventService } from '../../shared/services/domain/event.service';
import { Event } from '../../shared/models/index';

@Component({
  selector: 'app-calendar-events-column',
  templateUrl: './calendar-events-column.component.html',
})
export class CalendarEventsColumnComponent implements OnInit {
  @Input() selectedDate: Date;
  events: Event[] = [];
  isLoading = true;
  placeholders = [1, 2, 3];
  isAdmin = false; // Set this based on your authentication logic

  constructor(private eventService: EventService) { }

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    const dateString = this.selectedDate.toISOString().split('T')[0];
    this.eventService.getEvents('/events', { forDate: dateString }).subscribe({
      next: (response) => {
        this.events = response.events;
      },
      error: (error) => {
        console.error('Error fetching events:', error);
      },
      complete: () => {
        this.isLoading = false;
      },
    });
  }
}
