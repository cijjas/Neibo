import { Component, OnInit } from '@angular/core';
import { EventService } from '../../shared/services/domain/event.service';

@Component({
  selector: 'app-calendar-widget',
  templateUrl: './calendar-widget.component.html',
})
export class CalendarWidgetComponent implements OnInit {
  isLoading = true;
  currentDate: string = '';
  weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  days: Array<{ date: number; inactive: boolean; today: boolean; event: boolean }> = [];
  eventTimestamps: number[] = [];
  date = new Date();
  placeholders = Array(5).fill(0);

  events: Event

  constructor(private eventService: EventService) { }

  ngOnInit() {
    this.renderCalendar(); // Ensure the calendar renders immediately
    this.loadEventTimestamps(); // Fetch events asynchronously
  }

  private loadEventTimestamps(): void {
    this.eventService.getEvents('/endpoint/get-event-timestamps', {}).subscribe({
      next: (response) => {
        this.eventTimestamps = response.events.map((e) => new Date(e.eventDate).getTime());
        this.updateEventDays(); // Update event markers
      },
      error: (error) => {
        console.error('Error fetching event timestamps:', error);
      },
      complete: () => {
        this.isLoading = false;
      },
    });
  }

  private updateEventDays(): void {
    this.days = this.days.map((day) => {
      const dayDate = new Date(this.date.getFullYear(), this.date.getMonth(), day.date);
      const isEvent = this.eventTimestamps.some((timestamp) => {
        const eventDate = new Date(timestamp);
        return (
          dayDate.getDate() === eventDate.getDate() &&
          dayDate.getMonth() === eventDate.getMonth() &&
          dayDate.getFullYear() === eventDate.getFullYear()
        );
      });
      return { ...day, event: isEvent };
    });
  }

  renderCalendar(): void {
    const months = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December',
    ];

    const firstDayOfMonth = new Date(this.date.getFullYear(), this.date.getMonth(), 1).getDay();
    const lastDateOfMonth = new Date(this.date.getFullYear(), this.date.getMonth() + 1, 0).getDate();
    const lastDayOfMonth = new Date(this.date.getFullYear(), this.date.getMonth(), lastDateOfMonth).getDay();
    const lastDateOfLastMonth = new Date(this.date.getFullYear(), this.date.getMonth(), 0).getDate();

    this.days = [];

    // Previous month's days
    for (let i = firstDayOfMonth; i > 0; i--) {
      this.days.push({ date: lastDateOfLastMonth - i + 1, inactive: true, today: false, event: false });
    }

    // Current month's days
    for (let i = 1; i <= lastDateOfMonth; i++) {
      const currentDay = new Date(this.date.getFullYear(), this.date.getMonth(), i);
      const isToday = currentDay.toDateString() === new Date().toDateString();
      this.days.push({ date: i, inactive: false, today: isToday, event: false });
    }

    // Next month's days
    for (let i = lastDayOfMonth; i < 6; i++) {
      this.days.push({ date: i - lastDayOfMonth + 1, inactive: true, today: false, event: false });
    }

    this.currentDate = `${months[this.date.getMonth()]} ${this.date.getFullYear()}`;
    this.isLoading = false;
  }

  changeMonth(direction: number): void {
    this.date.setMonth(this.date.getMonth() + direction);
    this.renderCalendar();
    this.updateEventDays(); // Update event markers after month change
  }

  navigateToDay(day: { date: number }): void {
    // if (day.inactive) return; // Do nothing for inactive days
    const selectedDate = new Date(this.date.getFullYear(), this.date.getMonth(), day.date);
    window.location.href = `/calendar?timestamp=${selectedDate.getTime()}`;
  }
}
