import { Component, OnInit, Input } from '@angular/core';
import { EventService, LinkKey } from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-calendar-box',
  templateUrl: './calendar-box.component.html',
})
export class CalendarBoxComponent implements OnInit {
  @Input() selectedDate: Date;
  isLoading = true;
  currentDate: string = '';
  days: Array<any> = [];
  eventTimestamps: number[] = [];
  date = new Date();
  placeholders = Array(5).fill(0);
  isAdmin = false;
  selectedDay: any = null;
  weekDays: string[] = [];

  constructor(
    private eventService: EventService,
    private linkStorage: HateoasLinksService,
    private router: Router,
    private route: ActivatedRoute,
    private translate: TranslateService,
  ) {}

  ngOnInit() {
    // Initialize translated weekday labels.
    this.weekDays = [
      this.translate.instant('calendar.weekdays.Sunday'),
      this.translate.instant('calendar.weekdays.Monday'),
      this.translate.instant('calendar.weekdays.Tuesday'),
      this.translate.instant('calendar.weekdays.Wednesday'),
      this.translate.instant('calendar.weekdays.Thursday'),
      this.translate.instant('calendar.weekdays.Friday'),
      this.translate.instant('calendar.weekdays.Saturday'),
    ];

    this.route.queryParams.subscribe(params => {
      const dateParam = params['date'];
      if (dateParam) {
        const [year, month, day] = dateParam.split('-').map(Number);
        this.selectedDate = new Date(Date.UTC(year, month - 1, day));
      } else {
        const now = new Date();
        this.selectedDate = new Date(
          Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate()),
        );
      }

      // Set the calendar's date and selected day.
      this.date = new Date(this.selectedDate);
      this.selectedDay = {
        date: this.selectedDate.getUTCDate(),
        month: this.selectedDate.getUTCMonth(),
        year: this.selectedDate.getUTCFullYear(),
      };

      this.renderCalendar();
      this.loadEventTimestamps();
    });
  }

  private loadEventTimestamps(): void {
    const eventUrl = this.linkStorage.getLink(LinkKey.NEIGHBORHOOD_EVENTS);
    const datesInMonth = this.getDatesInMonth(
      this.date.getFullYear(),
      this.date.getMonth(),
    );
    const dateStrings = datesInMonth.map(
      date => date.toISOString().split('T')[0],
    );

    this.eventService.getEventsForDateRange(eventUrl, dateStrings).subscribe({
      next: allEvents => {
        this.eventTimestamps = allEvents.map(e =>
          new Date(e.eventDate).getTime(),
        );
        this.updateEventDays();
      },
      error: error => console.error('Error fetching event timestamps:', error),
      complete: () => (this.isLoading = false),
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
    this.days = this.days.map(day => {
      const dayDate = new Date(Date.UTC(day.year, day.month, day.date));
      const isEvent = this.eventTimestamps.some(timestamp => {
        const eventDate = new Date(timestamp);
        return (
          eventDate.getUTCFullYear() === dayDate.getUTCFullYear() &&
          eventDate.getUTCMonth() === dayDate.getUTCMonth() &&
          eventDate.getUTCDate() === dayDate.getUTCDate()
        );
      });
      return { ...day, event: isEvent };
    });
  }

  renderCalendar(): void {
    const year = this.date.getUTCFullYear();
    const month = this.date.getUTCMonth();

    const firstDayOfMonth = new Date(Date.UTC(year, month, 1)).getUTCDay();
    const lastDateOfMonth = new Date(Date.UTC(year, month + 1, 0)).getUTCDate();
    const lastDayOfMonth = new Date(
      Date.UTC(year, month, lastDateOfMonth),
    ).getUTCDay();
    const lastDateOfLastMonth = new Date(Date.UTC(year, month, 0)).getUTCDate();

    this.days = [];

    // Previous month's days (using UTC).
    for (let i = firstDayOfMonth; i > 0; i--) {
      let prevMonth = month - 1;
      let prevYear = year;
      if (prevMonth < 0) {
        prevMonth = 11;
        prevYear -= 1;
      }
      this.days.push({
        date: lastDateOfLastMonth - i + 1,
        month: prevMonth,
        year: prevYear,
        inactive: true,
        today: false,
        event: false,
      });
    }

    // Current month's days.
    for (let i = 1; i <= lastDateOfMonth; i++) {
      const currentDay = new Date(Date.UTC(year, month, i));
      const isToday =
        currentDay.toUTCString() ===
        new Date(
          Date.UTC(
            new Date().getUTCFullYear(),
            new Date().getUTCMonth(),
            new Date().getUTCDate(),
          ),
        ).toUTCString();
      this.days.push({
        date: i,
        month: month,
        year: year,
        inactive: false,
        today: isToday,
        event: false,
      });
    }

    // Next month's days.
    for (let i = lastDayOfMonth; i < 6; i++) {
      let nextMonth = month + 1;
      let nextYear = year;
      if (nextMonth > 11) {
        nextMonth = 0;
        nextYear += 1;
      }
      this.days.push({
        date: i - lastDayOfMonth + 1,
        month: nextMonth,
        year: nextYear,
        inactive: true,
        today: false,
        event: false,
      });
    }

    // Use translated month names to build the current date string.
    const monthKeys = [
      'calendar.month.January',
      'calendar.month.February',
      'calendar.month.March',
      'calendar.month.April',
      'calendar.month.May',
      'calendar.month.June',
      'calendar.month.July',
      'calendar.month.August',
      'calendar.month.September',
      'calendar.month.October',
      'calendar.month.November',
      'calendar.month.December',
    ];

    this.currentDate = `${this.translate.instant(monthKeys[month])} ${year}`;
    this.isLoading = false;
  }

  changeMonth(direction: number): void {
    this.date.setMonth(this.date.getMonth() + direction);
    this.renderCalendar();
    this.loadEventTimestamps();
  }

  navigateToDay(day: any): void {
    this.selectedDay = day;
    // Create a date (using local time at noon) for navigation.
    const selectedDate = new Date(day.year, day.month, day.date, 12);
    const year = selectedDate.getFullYear();
    const month = ('0' + (selectedDate.getMonth() + 1)).slice(-2);
    const dayDate = ('0' + selectedDate.getDate()).slice(-2);
    this.router.navigate(['/calendar'], {
      queryParams: { date: `${year}-${month}-${dayDate}` },
    });
  }
}
