import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CalendarService {
  private reloadCalendarSubject = new Subject<void>();
  calendarReload$ = this.reloadCalendarSubject.asObservable();

  triggerReload() {
    this.reloadCalendarSubject.next();
  }
}
