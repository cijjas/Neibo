import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CalendarWidgetComponent } from './calendar-widget.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of, Subject } from 'rxjs';
import { Router } from '@angular/router';

// Import the service types (adjust paths as needed)
import { CalendarService, EventService, LinkKey } from '@shared/index';
import { HateoasLinksService } from '@core/index';

describe('CalendarWidgetComponent', () => {
  let component: CalendarWidgetComponent;
  let fixture: ComponentFixture<CalendarWidgetComponent>;

  // Mocks & Spies
  const reloadSubject = new Subject<void>();
  // Stub calendarService with a reload subject
  const calendarServiceSpy = jasmine.createSpyObj('CalendarService', [
    'calendarReload$',
    'triggerReload',
  ]);
  Object.defineProperty(calendarServiceSpy, 'calendarReload$', {
    get: () => reloadSubject.asObservable(),
  });

  const eventServiceSpy = jasmine.createSpyObj('EventService', [
    'getEventsForDateRange',
  ]);
  eventServiceSpy.getEventsForDateRange.and.returnValue(of([])); // return empty events

  const linkStorageSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  linkStorageSpy.getLink.and.callFake((key: string) => {
    if (key === LinkKey.NEIGHBORHOOD_EVENTS) {
      return 'fake_event_url';
    }
    return '';
  });

  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [CalendarWidgetComponent], // It's standalone, so import it.
      providers: [
        { provide: EventService, useValue: eventServiceSpy },
        { provide: HateoasLinksService, useValue: linkStorageSpy },
        { provide: Router, useValue: routerSpy },
        { provide: CalendarService, useValue: calendarServiceSpy },
      ],
      // Override the template so we don’t process the real HTML (and avoid missing pipes).
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(CalendarWidgetComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CalendarWidgetComponent);
    component = fixture.componentInstance;
  });

  it('should initialize, trigger reload, and call loadEventTimestamps + renderCalendar', () => {
    // Spy on the internal methods
    spyOn(component as any, 'loadEventTimestamps').and.callThrough();
    spyOn(component, 'renderCalendar').and.callThrough();

    // When we detect changes, ngOnInit runs
    fixture.detectChanges();

    // We expect triggerReload to be called
    expect(calendarServiceSpy.triggerReload).toHaveBeenCalled();

    // Now simulate the reload event
    reloadSubject.next();

    // Expect loadEventTimestamps and renderCalendar to be called
    expect((component as any).loadEventTimestamps).toHaveBeenCalled();
    expect(component.renderCalendar).toHaveBeenCalled();
  });

  it('should changeMonth, update date, and re-render', () => {
    fixture.detectChanges(); // ngOnInit

    spyOn(component, 'renderCalendar');

    // Suppose the current month is X. We'll increment by 1.
    const oldMonth = component.date.getMonth();
    component.changeMonth(1);
    // The date’s month should be oldMonth + 1
    expect(component.date.getMonth()).toBe(oldMonth + 1);

    // Also, changeMonth calls loadEventTimestamps and renderCalendar
    // We can spy on them if we want even more detail:
    expect(component.renderCalendar).toHaveBeenCalled();
  });

  it('should navigate to day with correct query param', () => {
    fixture.detectChanges();

    // Sample day object
    const dayObj = {
      date: 15,
      month: 6,
      year: 2023,
      inactive: false,
    };

    component.navigateToDay(dayObj);

    // The param should be 2023-07-15 (month is 0-based => +1 => "07")
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/calendar'], {
      queryParams: { date: '2023-07-15' },
    });
  });
});
