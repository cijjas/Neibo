import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
  waitForAsync,
} from '@angular/core/testing';
import { CalendarBoxComponent } from './calendar-box.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';
import { Router, ActivatedRoute } from '@angular/router';

import { EventService, LinkKey } from '@shared/index';
import { HateoasLinksService } from '@core/index';

describe('CalendarBoxComponent - Initialization', () => {
  let component: CalendarBoxComponent;
  let fixture: ComponentFixture<CalendarBoxComponent>;

  // Stub ActivatedRoute: simulate query parameters with a date.
  const activatedRouteStub = {
    queryParams: of({ date: '2020-05-15' }),
  };

  // Stub Router.
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // Stub HateoasLinksService to return a dummy events URL.
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  linkServiceSpy.getLink.and.callFake((key: string) => {
    if (key === LinkKey.NEIGHBORHOOD_EVENTS) {
      return 'dummy_events_url';
    }
    return '';
  });

  // Stub EventService with getEventsForDateRange.
  // Dummy event: its eventDate matches May 15, 2020.
  const dummyEvents = [{ eventDate: '2020-05-15T00:00:00.000Z' }];
  const eventServiceSpy = jasmine.createSpyObj('EventService', [
    'getEventsForDateRange',
  ]);
  eventServiceSpy.getEventsForDateRange.and.returnValue(of(dummyEvents));

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CalendarBoxComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: Router, useValue: routerSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: EventService, useValue: eventServiceSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA], // Ignore unknown elements/pipes
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CalendarBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should initialize correctly with query date, render calendar, load event timestamps, and set isLoading to false', fakeAsync(() => {
    // The query parameter "2020-05-15" should set selectedDate to May 15, 2020 in UTC.
    const expectedDate = new Date(Date.UTC(2020, 4, 15));
    expect(component.selectedDate.toISOString()).toEqual(
      expectedDate.toISOString(),
    );

    // The currentDate string (e.g., "May 2020") should contain "May" and "2020".
    expect(component.currentDate).toContain('May');
    expect(component.currentDate).toContain('2020');

    // After loadEventTimestamps() finishes (via tick), eventTimestamps should include the timestamp for May 15, 2020.
    tick(); // Flush asynchronous operations
    const expectedTimestamp = new Date('2020-05-15T00:00:00.000Z').getTime();
    expect(component.eventTimestamps).toContain(expectedTimestamp);

    // The days array should be populated (e.g. calendar rendered).
    expect(component.days.length).toBeGreaterThan(0);

    // isLoading should be false at the end of initialization.
    expect(component.isLoading).toBeFalse();
  }));
});
