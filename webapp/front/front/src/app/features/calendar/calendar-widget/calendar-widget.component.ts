import { Component, OnInit } from '@angular/core';
import { CalendarService, EventService, LinkKey } from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-calendar-widget',
  templateUrl: './calendar-widget.component.html',
  standalone: true,
  imports: [CommonModule, RouterModule],
})
export class CalendarWidgetComponent implements OnInit {
  isLoading = true;
  currentDate: string = '';
  weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  days: Array<{
    date: number;
    month: number;
    year: number;
    inactive: boolean;
    today: boolean;
    event: boolean;
  }> = [];

  eventTimestamps: number[] = [];
  date = new Date();
  placeholders = Array(5).fill(0);

  constructor(
    private eventService: EventService,
    private linkStorage: HateoasLinksService,
    private router: Router,
    private calendarService: CalendarService
  ) {}

  ngOnInit() {
    this.calendarService.calendarReload$.subscribe(() => {
      this.loadEventTimestamps();

      this.renderCalendar();
    });
    this.calendarService.triggerReload();
  }

  private loadEventTimestamps(): void {
    const eventUrl = this.linkStorage.getLink(LinkKey.NEIGHBORHOOD_EVENTS);
    const datesInMonth = this.getDatesInMonth(
      this.date.getFullYear(),
      this.date.getMonth()
    );

    const dateStrings = datesInMonth.map((date) => {
      return date.toISOString().split('T')[0];
    });

    this.eventService.getEventsForDateRange(eventUrl, dateStrings).subscribe({
      next: (allEvents) => {
        this.eventTimestamps = allEvents.map((e) =>
          new Date(e.eventDate).getTime()
        );
        this.updateEventDays();
      },
      error: (error) => {
        console.error('Error fetching event timestamps:', error);
      },
      complete: () => {
        this.isLoading = false;
      },
    });
  }

  private getDatesInMonth(year: number, month: number): Date[] {
    const date = new Date(year, month, 1);
    const dates = [];
    while (date.getMonth() === month) {
      dates.push(new Date(date));
      date.setDate(date.getDate() + 1);
    }
    return dates;
  }

  private updateEventDays(): void {
    this.days = this.days.map((day) => {
      // Create a date at midnight in UTC for the calendar day
      const dayDate = new Date(Date.UTC(day.year, day.month, day.date));

      // Check if there is an event on this exact day
      const isEvent = this.eventTimestamps.some((timestamp) => {
        const eventDate = new Date(timestamp);
        // Compare only the date parts
        return (
          dayDate.getUTCFullYear() === eventDate.getUTCFullYear() &&
          dayDate.getUTCMonth() === eventDate.getUTCMonth() &&
          dayDate.getUTCDate() === eventDate.getUTCDate()
        );
      });

      return { ...day, event: isEvent };
    });
  }

  renderCalendar(): void {
    const months = [
      'January',
      'February',
      'March',
      'April',
      'May',
      'June',
      'July',
      'August',
      'September',
      'October',
      'November',
      'December',
    ];

    const firstDayOfMonth = new Date(
      this.date.getFullYear(),
      this.date.getMonth(),
      1
    ).getDay();
    const lastDateOfMonth = new Date(
      this.date.getFullYear(),
      this.date.getMonth() + 1,
      0
    ).getDate();
    const lastDayOfMonth = new Date(
      this.date.getFullYear(),
      this.date.getMonth(),
      lastDateOfMonth
    ).getDay();
    const lastDateOfLastMonth = new Date(
      this.date.getFullYear(),
      this.date.getMonth(),
      0
    ).getDate();

    this.days = [];

    // Previous month's days
    for (let i = firstDayOfMonth; i > 0; i--) {
      let prevMonth = this.date.getMonth() - 1;
      let year = this.date.getFullYear();
      if (prevMonth < 0) {
        prevMonth = 11;
        year -= 1;
      }
      this.days.push({
        date: lastDateOfLastMonth - i + 1,
        month: prevMonth,
        year: year,
        inactive: true,
        today: false,
        event: false,
      });
    }

    // Current month's days
    for (let i = 1; i <= lastDateOfMonth; i++) {
      const currentDay = new Date(
        this.date.getFullYear(),
        this.date.getMonth(),
        i
      );
      const isToday = currentDay.toDateString() === new Date().toDateString();
      this.days.push({
        date: i,
        month: this.date.getMonth(),
        year: this.date.getFullYear(),
        inactive: false,
        today: isToday,
        event: false,
      });
    }

    // Next month's days
    for (let i = lastDayOfMonth; i < 6; i++) {
      let nextMonth = this.date.getMonth() + 1;
      let year = this.date.getFullYear();
      if (nextMonth > 11) {
        nextMonth = 0;
        year += 1;
      }
      this.days.push({
        date: i - lastDayOfMonth + 1,
        month: nextMonth,
        year: year,
        inactive: true,
        today: false,
        event: false,
      });
    }

    this.currentDate = `${
      months[this.date.getMonth()]
    } ${this.date.getFullYear()}`;
    this.isLoading = false;
  }

  changeMonth(direction: number): void {
    // Update the current month
    this.date.setMonth(this.date.getMonth() + direction);

    // Re-render the calendar for the new month
    this.renderCalendar();

    // Request events for the new month
    this.loadEventTimestamps();
  }

  navigateToDay(day: {
    date: number;
    month: number;
    year: number;
    inactive: boolean;
  }): void {
    const selectedDate = new Date(Date.UTC(day.year, day.month, day.date));
    const year = selectedDate.getUTCFullYear();
    const month = ('0' + (selectedDate.getUTCMonth() + 1)).slice(-2);
    const dayDate = ('0' + selectedDate.getUTCDate()).slice(-2);

    this.router.navigate(['/calendar'], {
      queryParams: { date: `${year}-${month}-${dayDate}` },
    });
  }
}
