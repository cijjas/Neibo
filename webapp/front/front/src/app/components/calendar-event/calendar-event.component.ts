import { Component, Input } from '@angular/core';
import { Event } from '../../shared/models/index';

@Component({
  selector: 'app-calendar-event',
  templateUrl: './calendar-event.component.html',
})
export class CalendarEventComponent {
  @Input() event: Event;
  @Input() isAdmin: boolean;
  @Input() selectedDate: Date;

  get selectedTimestamp(): number {
    return this.selectedDate.getTime();
  }
}
