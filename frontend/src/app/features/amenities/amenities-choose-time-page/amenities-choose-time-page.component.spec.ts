import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AmenitiesChooseTimePageComponent } from './amenities-choose-time-page.component';
import { FormBuilder, ReactiveFormsModule, FormArray } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { of, Subject } from 'rxjs';
import {
  AmenityService,
  BookingService,
  ShiftService,
  LinkKey,
} from '@shared/index';
import { HateoasLinksService, UserSessionService } from '@core/index';
import { CUSTOM_ELEMENTS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

// Fake translate pipe so that the template does not complain about the missing translate pipe.
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string, ...args: any[]): string {
    return value;
  }
}

// Utility function to simulate queryParamMap.
function fakeQueryParamMap(params: { [key: string]: string | null }) {
  return of({
    get: (key: string) => params[key] || null,
  });
}

describe('AmenitiesChooseTimePageComponent', () => {
  let component: AmenitiesChooseTimePageComponent;
  let fixture: ComponentFixture<AmenitiesChooseTimePageComponent>;

  // Mocks for services:
  const mockAmenityService = {
    getAmenity: jasmine
      .createSpy('getAmenity')
      .and.returnValue(
        of({
          name: 'Test Amenity',
          description: 'desc',
          availableShifts: [],
          self: 'amenity_self',
        }),
      ),
  };

  // Two shifts: one available and one taken.
  const mockShifts = [
    {
      startTime: '08:00:00',
      endTime: '09:00:00',
      day: '2020-01-01',
      taken: false,
      self: 'shift1',
    },
    {
      startTime: '09:00:00',
      endTime: '10:00:00',
      day: '2020-01-01',
      taken: true,
      self: 'shift2',
    },
  ];
  const mockShiftService = {
    getShifts: jasmine.createSpy('getShifts').and.returnValue(of(mockShifts)),
  };

  const mockBookingService = {
    createBooking: jasmine.createSpy('createBooking').and.returnValue(of({})),
  };

  const mockLinkService = {
    getLink: jasmine.createSpy('getLink').and.callFake((key: string) => {
      if (key === LinkKey.USER_SELF) {
        return 'user_self_link';
      }
      return '';
    }),
  };

  const mockUserSessionService = {};

  // Simple spies for Router and Location.
  const mockRouter = {
    navigate: jasmine.createSpy('navigate'),
  };

  const mockLocation = {
    back: jasmine.createSpy('back'),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [AmenitiesChooseTimePageComponent, FakeTranslatePipe],
      providers: [
        FormBuilder,
        { provide: AmenityService, useValue: mockAmenityService },
        { provide: ShiftService, useValue: mockShiftService },
        { provide: BookingService, useValue: mockBookingService },
        { provide: HateoasLinksService, useValue: mockLinkService },
        { provide: UserSessionService, useValue: mockUserSessionService },
        { provide: Router, useValue: mockRouter },
        { provide: Location, useValue: mockLocation },
        {
          provide: ActivatedRoute,
          useValue: {
            // Simulate query parameters with amenityUrl and date.
            queryParamMap: fakeQueryParamMap({
              amenityUrl: 'amenity_url',
              date: '2020-01-01',
            }),
          },
        },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(AmenitiesChooseTimePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load data on init when amenityUrl and date are provided', () => {
    // After initialization, amenityUrl and date should be set.
    expect(component.amenityUrl).toEqual('amenity_url');
    expect(component.date).toEqual('2020-01-01');

    // Amenity service should be called and amenityName set.
    expect(mockAmenityService.getAmenity).toHaveBeenCalledWith('amenity_url');
    expect(component.amenityName).toEqual('Test Amenity');

    // Shift service should be called and bookings set.
    expect(mockShiftService.getShifts).toHaveBeenCalledWith({
      forAmenity: 'amenity_url',
      forDate: '2020-01-01',
    });
    expect(component.bookings.length).toEqual(mockShifts.length);

    // Form array should have one control per booking.
    const formArray = component.shiftsForm.get('selectedShifts') as FormArray;
    expect(formArray.controls.length).toEqual(mockShifts.length);

    // The first control should be enabled (available), the second disabled (taken).
    expect(formArray.controls[0].disabled).toBeFalse();
    expect(formArray.controls[1].disabled).toBeTrue();

    // isLoading should be false after data loads.
    expect(component.isLoading).toBeFalse();
  });

  it('should create a booking and show reservation dialog on reserve', () => {
    // Mark the first (available) shift as selected.
    const formArray = component.shiftsForm.get('selectedShifts') as FormArray;
    formArray.setValue([true, false]);

    component.onReserve();

    // Booking service should be called with the proper parameters.
    expect(mockBookingService.createBooking).toHaveBeenCalledWith(
      'amenity_url',
      '2020-01-01',
      ['shift1'],
      'user_self_link',
    );

    // After a successful booking, the formattedShiftTimes should be built.
    // Times should be formatted to '08:00' and '09:00' (removing seconds).
    const expectedFormatted = `<p>08:00 - 09:00</p>`;
    expect(component.formattedShiftTimes).toEqual(expectedFormatted);
    expect(component.showReservationDialog).toBeTrue();
  });

  it('should return correct value for isReserveDisabled', () => {
    const formArray = component.shiftsForm.get('selectedShifts') as FormArray;
    // No shift selected => disabled.
    formArray.setValue([false, false]);
    expect(component.isReserveDisabled()).toBeTrue();

    // At least one shift selected => not disabled.
    formArray.setValue([true, false]);
    expect(component.isReserveDisabled()).toBeFalse();
  });

  it('should call location.back() when goBack is called', () => {
    component.goBack();
    expect(mockLocation.back).toHaveBeenCalled();
  });

  it('should navigate to /amenities when redirectToReservations is called', () => {
    component.redirectToReservations();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/amenities']);
    expect(component.showReservationDialog).toBeFalse();
  });
});
