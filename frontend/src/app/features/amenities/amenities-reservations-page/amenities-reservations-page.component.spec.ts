import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AmenitiesReservationsPageComponent } from './amenities-reservations-page.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import {
  AmenityService,
  ShiftService,
  Amenity,
  Shift,
  BookingService,
} from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { TranslateService } from '@ngx-translate/core';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('AmenitiesReservationsPageComponent', () => {
  let component: AmenitiesReservationsPageComponent;
  let fixture: ComponentFixture<AmenitiesReservationsPageComponent>;

  // Dummy response for amenities.
  const dummyAmenitiesResponse = {
    amenities: [
      {
        name: 'Amenity 1',
        self: 'amenity1',
        description: 'desc',
        availableShifts: [],
      },
    ],
    currentPage: 2,
    totalPages: 5,
  };

  // Dummy shifts.
  const dummyShifts: Shift[] = [
    {
      startTime: '08:00:00',
      endTime: '09:00:00',
      day: 'Monday',
      taken: false,
      self: 'shift1',
    },
    {
      startTime: '10:00:00',
      endTime: '11:00:00',
      day: 'Tuesday',
      taken: false,
      self: 'shift2',
    },
    {
      startTime: '12:00:00',
      endTime: '13:00:00',
      day: 'Monday',
      taken: false,
      self: 'shift3',
    },
  ];

  // Fake services.
  const fakeAmenityService = {
    getAmenities: jasmine
      .createSpy('getAmenities')
      .and.returnValue(of(dummyAmenitiesResponse)),
  };
  const fakeShiftService = {
    getShifts: jasmine.createSpy('getShifts').and.returnValue(of(dummyShifts)),
  };

  // Other services
  const fakeBookingService = {};
  const fakeLinkService = {};
  const fakeTranslateService = {
    instant: jasmine.createSpy('instant').and.callFake((key: string) => {
      const map: Record<string, string> = {
        'ADMIN-AMENITIES-PAGE.MON': 'MON',
        'ADMIN-AMENITIES-PAGE.TUE': 'TUE',
        'ADMIN-AMENITIES-PAGE.WED': 'WED',
        'ADMIN-AMENITIES-PAGE.THU': 'THU',
        'ADMIN-AMENITIES-PAGE.FRI': 'FRI',
        'ADMIN-AMENITIES-PAGE.SAT': 'SAT',
        'ADMIN-AMENITIES-PAGE.SUN': 'SUN',
      };
      return map[key] || key;
    }),
  };

  const fakeRouter = {
    navigate: jasmine.createSpy('navigate'),
  };

  const fakeActivatedRoute = {
    queryParams: of({ page: '3', size: '10' }),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [AmenitiesReservationsPageComponent],
      providers: [
        FormBuilder,
        { provide: AmenityService, useValue: fakeAmenityService },
        { provide: ShiftService, useValue: fakeShiftService },
        { provide: BookingService, useValue: fakeBookingService },
        { provide: HateoasLinksService, useValue: fakeLinkService },
        { provide: TranslateService, useValue: fakeTranslateService },
        { provide: Router, useValue: fakeRouter },
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
      ],
      // Using NO_ERRORS_SCHEMA to ignore any unknown elements (since we override the template)
      schemas: [NO_ERRORS_SCHEMA],
    })
      // Override the template so that we don’t have the problematic binding for "amenity".
      .overrideTemplate(
        AmenitiesReservationsPageComponent,
        `
        <form [formGroup]="reservationForm">
          <!-- We omit the control for "amenity" to avoid the value accessor error -->
          <input formControlName="date" />
        </form>
      `,
      )
      .compileComponents();

    fixture = TestBed.createComponent(AmenitiesReservationsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should initialize the form and load amenities and shifts', () => {
    // The form should have "amenity" and "date" controls.
    expect(component.reservationForm.contains('amenity')).toBeTrue();
    expect(component.reservationForm.contains('date')).toBeTrue();
    // Query params: initially page=3 and size=10, but the service response sets currentPage.
    expect(component.currentPage).toEqual(dummyAmenitiesResponse.currentPage);
    expect(component.pageSize).toEqual(10);
    // Verify amenities loaded.
    expect(component.amenities).toEqual(dummyAmenitiesResponse.amenities);
    expect(component.totalPages).toEqual(dummyAmenitiesResponse.totalPages);
    expect(component.isLoading).toBeFalse();
    // Verify shifts loaded and unique values computed.
    expect(component.allShifts).toEqual(dummyShifts);
    expect(component.uniqueDays).toEqual(['Monday', 'Tuesday']);
    expect(component.uniqueTimes).toEqual(['08:00:00', '10:00:00', '12:00:00']);
  });

  it('should update query params and load amenities on page change', () => {
    spyOn(component, 'loadAmenities').and.callThrough();
    component.onPageChange(4);
    // Check that the router was called with the intended page (4)
    expect(fakeRouter.navigate).toHaveBeenCalledWith([], {
      relativeTo: fakeActivatedRoute,
      queryParams: { page: 4, size: component.pageSize },
      queryParamsHandling: 'merge',
    });
    expect(component.loadAmenities).toHaveBeenCalled();
    // Because the dummy service response sets currentPage to 2, the component.currentPage will be updated to 2.
    expect(component.currentPage).toEqual(dummyAmenitiesResponse.currentPage);
  });

  it('should navigate on valid form submission', () => {
    // For this test we set the "amenity" control value to an object with a "self" property.
    const amenityObj: Amenity = {
      name: 'Amenity 1',
      self: 'amenity1',
      description: 'desc',
      availableShifts: [],
    };
    component.reservationForm.setValue({
      amenity: amenityObj,
      date: '2020-01-01',
    });
    component.onSubmit();
    expect(fakeRouter.navigate).toHaveBeenCalledWith(
      ['/amenities', 'choose-time'],
      {
        queryParams: { amenityUrl: 'amenity1', date: '2020-01-01' },
      },
    );
  });

  it('should mark the form as touched on invalid submission', () => {
    spyOn(component.reservationForm, 'markAllAsTouched');
    // Set invalid values.
    component.reservationForm.setValue({ amenity: '', date: '' });
    component.onSubmit();
    expect(component.reservationForm.markAllAsTouched).toHaveBeenCalled();
  });

  it('formatTime should convert "HH:mm:ss" to "HH:mm"', () => {
    expect(component.formatTime('08:30:00')).toEqual('08:30');
  });
});
