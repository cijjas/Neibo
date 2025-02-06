import {
  ComponentFixture,
  TestBed,
  waitForAsync,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { CalendarEventPageComponent } from './calendar-event-page.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of, throwError } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { HateoasLinksService, ToastService } from '@core/index';
import {
  AttendanceService,
  EventService,
  Event,
  Attendance,
  LinkKey,
} from '@shared/index';

describe('CalendarEventPageComponent', () => {
  let component: CalendarEventPageComponent;
  let fixture: ComponentFixture<CalendarEventPageComponent>;

  // Dummy event for initialization.
  const dummyEvent: Event = {
    name: 'Test Event',
    description: 'An event for testing',
    eventDate: new Date('2023-05-10T12:00:00Z'),
    eventDateDisplay: 'May 10, 2023',
    startTime: '12:00',
    endTime: '13:00',
    duration: 60,
    attendees: '',
    attendeesCount: 2,
    self: 'event_self_link',
  };

  // Dummy attendance list.
  const dummyAttendances: Attendance[] = [
    {
      user: { self: 'user_self_1', name: 'TestUser1' } as any,
      event: dummyEvent,
      self: 'attendance1',
    },
    {
      user: { self: 'user_self_2', name: 'TestUser2' } as any,
      event: dummyEvent,
      self: 'attendance2',
    },
  ];

  // Spies:
  const activatedRouteSpy = {
    data: of({ event: dummyEvent }),
    queryParamMap: of({ get: (_key: string) => null }), // no attendancePage param initially
    queryParams: of({}), // or empty
  };

  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  linkServiceSpy.getLink.and.callFake((key: string) => {
    if (key === LinkKey.USER_SELF) {
      return 'user_self_link';
    }
    return '';
  });

  const eventServiceSpy = jasmine.createSpyObj('EventService', ['getEvent']);
  // not used in ngOnInit directly (since route data is used), but available for future use.

  // Setup attendance service
  const attendanceServiceSpy = jasmine.createSpyObj('AttendanceService', [
    'getAttendances',
    'createAttendance',
    'deleteAttendance',
  ]);

  const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);

  const fakeTranslateService = {
    instant: (key: string) => key,
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CalendarEventPageComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: EventService, useValue: eventServiceSpy },
        { provide: AttendanceService, useValue: attendanceServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: TranslateService, useValue: fakeTranslateService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(CalendarEventPageComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CalendarEventPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should initialize and load the event, load attendees, and check user attendance', fakeAsync(() => {
    // For user attendance, the attendanceService is called with forEvent=event_self_link, page=1.
    const dummyUserAttendance = { attendances: [{ self: 'some_attendance' }] };
    attendanceServiceSpy.getAttendances.and.returnValue(
      of(dummyUserAttendance),
    );

    // For the main attendee list
    const dummyAttendeeList = {
      attendances: dummyAttendances,
      currentPage: 1,
      totalPages: 1,
    };
    attendanceServiceSpy.getAttendances.and.returnValue(of(dummyAttendeeList));

    // Force another call to loadAttendance (and user attendance) to simulate
    // the data subscription finishing.
    component.loadAttendance();
    component.checkUserAttendance();
    tick();

    // The event from route data
    expect(component.event).toEqual(dummyEvent);

    // The main attendee list
    expect(component.attendees).toEqual(dummyAttendances);
    // The user attendance check
    expect(component.willAttend).toBeTrue(); // because dummyUserAttendance has 1 item
  }));

  it('should reload attendance if query param attendancePage changes', fakeAsync(() => {
    // Suppose the route param changes from page=1 to page=2
    const dummyAttendeePage2 = {
      attendances: [dummyAttendances[0]],
      currentPage: 2,
      totalPages: 2,
    };
    attendanceServiceSpy.getAttendances.and.returnValue(of(dummyAttendeePage2));
    // Simulate new param: We manually call loadAttendance, which is done in route.queryParamMap subscription
    component.attendanceCurrentPage = 2;
    component.loadAttendance();
    tick();

    // We expect the updated attendance
    expect(component.attendees).toEqual(dummyAttendeePage2.attendances);
    expect(component.attendanceCurrentPage).toEqual(
      dummyAttendeePage2.currentPage,
    );
    expect(component.attendanceTotalPages).toEqual(
      dummyAttendeePage2.totalPages,
    );
  }));

  it('should create attendance when attendEvent is called', fakeAsync(() => {
    // Suppose createAttendance returns success.
    attendanceServiceSpy.createAttendance.and.returnValue(of({}));
    // Suppose after attendance is created, we reload attendees
    const updatedAttendeeList = {
      attendances: [...dummyAttendances, { self: 'new_attendance' } as any],
      currentPage: 1,
      totalPages: 1,
    };
    attendanceServiceSpy.getAttendances.and.returnValue(
      of(updatedAttendeeList),
    );

    component.event = { ...dummyEvent, attendeesCount: 2 };
    component.willAttend = false;

    component.attendEvent();
    tick();

    expect(component.willAttend).toBeTrue();
    // The count increments by 1
    expect(component.event.attendeesCount).toBe(3);
    expect(toastServiceSpy.showToast).toHaveBeenCalledWith(
      'CALENDAR-EVENT-PAGE.ATTENDANCE_TO_EVENT_CONFIRMED',
      'success',
    );
    // Check that loadAttendance was called
    expect(component.attendees.length).toBe(3);
  }));

  it('should delete attendance when unattendEvent is called', fakeAsync(() => {
    // Suppose deleteAttendance returns success.
    attendanceServiceSpy.deleteAttendance.and.returnValue(of({}));
    // Suppose after attendance is removed, we reload attendees
    const updatedAttendeeList = {
      attendances: [],
      currentPage: 1,
      totalPages: 1,
    };
    attendanceServiceSpy.getAttendances.and.returnValue(
      of(updatedAttendeeList),
    );

    component.event = { ...dummyEvent, attendeesCount: 2 };
    component.willAttend = true;

    component.unattendEvent();
    tick();

    expect(component.willAttend).toBeFalse();
    // The count decrements by 1 (from 2 to 1)
    expect(component.event.attendeesCount).toBe(1);
    expect(toastServiceSpy.showToast).toHaveBeenCalledWith(
      'CALENDAR-EVENT-PAGE.YOU_WERE_UNLISTED_FROM_THE_EVENT',
      'success',
    );
    // Confirm loadAttendance
    expect(component.attendees.length).toBe(0);
  }));

  it('should change attendance page on onAttendancePageChange and reload attendees', () => {
    // Stub out a new page for attendance
    const newAttendeesResponse = {
      attendances: [dummyAttendances[0]],
      currentPage: 2,
      totalPages: 3,
    };
    attendanceServiceSpy.getAttendances.and.returnValue(
      of(newAttendeesResponse),
    );

    component.onAttendancePageChange(2);

    expect(component.attendanceCurrentPage).toBe(2);
    expect(component.attendees).toEqual(newAttendeesResponse.attendances);
    expect(component.attendanceTotalPages).toBe(
      newAttendeesResponse.totalPages,
    );
  });
});
