import {
  ComponentFixture,
  TestBed,
  waitForAsync,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { CalendarEventsColumnComponent } from './calendar-events-column.component';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { of, throwError } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

// Services/models (adjust import paths as needed)
import { EventService, LinkKey, Event, Role } from '@shared/index';
import {
  HateoasLinksService,
  ConfirmationService,
  ToastService,
  UserSessionService,
} from '@core/index';
import { TranslateService } from '@ngx-translate/core';

@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

// Dummy event
const dummyEvent: Event = {
  name: 'Test Event',
  description: 'A test event',
  eventDate: new Date('2023-05-15T12:00:00Z'),
  eventDateDisplay: 'May 15, 2023',
  startTime: '12:00',
  endTime: '13:00',
  duration: 60,
  attendees: '',
  attendeesCount: 10,
  self: 'event_self_link',
};

// Stub services
const eventServiceSpy = jasmine.createSpyObj('EventService', [
  'getEvents',
  'deleteEvent',
]);
const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', ['getLink']);
const confirmationServiceSpy = jasmine.createSpyObj('ConfirmationService', [
  'askForConfirmation',
]);
const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);
const userSessionSpy = jasmine.createSpyObj('UserSessionService', [
  'getCurrentRole',
]);

describe('CalendarEventsColumnComponent', () => {
  let component: CalendarEventsColumnComponent;
  let fixture: ComponentFixture<CalendarEventsColumnComponent>;
  let routerSpy: jasmine.SpyObj<Router>;

  // Provide a fake route that has queryParams for date and page
  const activatedRouteStub = {
    queryParams: of({ date: '2023-05-15', page: '2' }),
  };

  beforeEach(waitForAsync(() => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    // For link service, return some dummy link
    linkServiceSpy.getLink.and.callFake((key: string) => {
      if (key === LinkKey.NEIGHBORHOOD_EVENTS) {
        return 'dummy_events_url';
      }
      return '';
    });

    // By default, eventService.getEvents returns a dummy success with one event
    const dummyResponse = {
      events: [dummyEvent],
      totalPages: 3,
    };
    eventServiceSpy.getEvents.and.returnValue(of(dummyResponse));

    // Confirmation by default: auto-confirm for testing
    confirmationServiceSpy.askForConfirmation.and.returnValue(of(true));

    userSessionSpy.getCurrentRole.and.returnValue(Role.NEIGHBOR);

    TestBed.configureTestingModule({
      declarations: [CalendarEventsColumnComponent, FakeTranslatePipe],
      providers: [
        { provide: EventService, useValue: eventServiceSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: ConfirmationService, useValue: confirmationServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: UserSessionService, useValue: userSessionSpy },
        {
          provide: TranslateService,
          useValue: { instant: (key: string) => key },
        },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: Router, useValue: routerSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(
        CalendarEventsColumnComponent,
        `<div>Dummy Template</div>`,
      )
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CalendarEventsColumnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should initialize with queryParams, set date/page, load events, and check admin role', fakeAsync(() => {
    // After ngOnInit:
    // We expect the date from queryParam '2023-05-15'
    expect(component.selectedDate.toISOString().slice(0, 10)).toBe(
      '2023-05-15',
    );
    // We expect the page from queryParam '2'
    expect(component.currentPage).toBe(2);

    // By default, getEvents returns one event
    expect(component.events.length).toBe(1);
    expect(component.events[0].name).toBe('Test Event');
    // totalPages from dummyResponse is 3
    expect(component.totalPages).toBe(3);

    // userSessionService role is Roles.USER by default
    expect(component.isAdmin).toBeFalse();
  }));

  it('should update the page, navigate, and reload events on onPageChange()', fakeAsync(() => {
    // Setup a new dummy response for the new page
    const newResponse = {
      events: [{ ...dummyEvent, name: 'Page3 Event' }],
      totalPages: 5,
    };
    eventServiceSpy.getEvents.and.returnValue(of(newResponse));

    // The user calls onPageChange(3)
    component.onPageChange(3);
    tick();

    // We expect the router to navigate with { page: 3, date: '2023-05-15' }, merging query params
    expect(routerSpy.navigate).toHaveBeenCalledWith([], {
      queryParams: { page: 3, date: '2023-05-15' },
      queryParamsHandling: 'merge',
    });
    // The eventService is called again and sets new events
    expect(component.events.length).toBe(1);
    expect(component.events[0].name).toBe('Page3 Event');
    expect(component.totalPages).toBe(5);
  }));

  it('should delete event after confirmation and reload events', fakeAsync(() => {
    // For the second fetch after deletion, let's return an empty list
    const afterDeleteResponse = { events: [], totalPages: 1 };
    eventServiceSpy.deleteEvent.and.returnValue(of({}));
    eventServiceSpy.getEvents.and.returnValue(of(afterDeleteResponse));

    // Call deleteEvent
    component.deleteEvent(dummyEvent);
    tick();

    // Because askForConfirmation returned true, deleteEvent is called
    expect(eventServiceSpy.deleteEvent).toHaveBeenCalledWith(dummyEvent.self);

    // Then we reload events
    expect(component.events.length).toBe(0);
    expect(component.totalPages).toBe(1);

    // A success toast is shown
    expect(toastServiceSpy.showToast).toHaveBeenCalledWith(
      'CALENDAR-EVENTS-COLUMN.EVENT_DELETED_SUCCESSFULLY',
      'success',
    );
  }));

  it('should handle delete event error', fakeAsync(() => {
    // Suppose deleteEvent fails
    eventServiceSpy.deleteEvent.and.returnValue(
      throwError(() => new Error('Delete failed')),
    );

    // We do not change getEvents yet
    component.deleteEvent(dummyEvent);
    tick();

    expect(eventServiceSpy.deleteEvent).toHaveBeenCalledWith(dummyEvent.self);
    // On error, a toast is shown with 'CALENDAR-EVENTS-COLUMN.FAILED_TO_DELETE_EVENT'
    expect(toastServiceSpy.showToast).toHaveBeenCalledWith(
      'CALENDAR-EVENTS-COLUMN.FAILED_TO_DELETE_EVENT',
      'error',
    );
  }));

  it('should set totalPages=1 if no events are returned', fakeAsync(() => {
    // Suppose getEvents returns no events
    const emptyResponse = { events: [], totalPages: 2 };
    eventServiceSpy.getEvents.and.returnValue(of(emptyResponse));

    // Reload
    component.loadEventsForSelectedDate();
    tick();

    // The events array is empty, so we override totalPages to 1
    expect(component.events.length).toBe(0);
    expect(component.totalPages).toBe(1);
  }));
});
