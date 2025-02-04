import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AmenitiesReservationsListComponent } from './amenities-reservations-list.component';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { BookingService, Booking, LinkKey } from '@shared/index';
import {
  HateoasLinksService,
  ToastService,
  ConfirmationService,
} from '@core/index';
import { TranslateService } from '@ngx-translate/core';
import { CUSTOM_ELEMENTS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

// Fake translate pipe so that the template can use "translate" without error.
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string, ...args: any[]): string {
    return value;
  }
}

describe('AmenitiesReservationsListComponent', () => {
  let component: AmenitiesReservationsListComponent;
  let fixture: ComponentFixture<AmenitiesReservationsListComponent>;

  // Mocks for services:
  const mockBookingService = {
    getBookings: jasmine.createSpy('getBookings'),
    deleteBooking: jasmine.createSpy('deleteBooking'),
  };

  const mockLinkService = {
    getLink: jasmine.createSpy('getLink').and.callFake((key: string) => {
      // Return dummy links based on key.
      if (key === LinkKey.NEIGHBORHOOD_BOOKINGS) {
        return 'bookings_url';
      }
      if (key === LinkKey.USER_SELF) {
        return 'user_self_link';
      }
      return '';
    }),
  };

  const mockToastService = {
    showToast: jasmine.createSpy('showToast'),
  };

  const mockConfirmationService = {
    askForConfirmation: jasmine.createSpy('askForConfirmation'),
  };

  const mockTranslateService = {
    instant: jasmine
      .createSpy('instant')
      .and.callFake((key: string, params?: any) => {
        // Optionally include params in the returned string if needed.
        return key;
      }),
  };

  // ActivatedRoute with a queryParams observable.
  const mockActivatedRoute = {
    queryParams: of({ reservationsPage: '1' }),
  };

  const mockRouter = {
    navigate: jasmine.createSpy('navigate'),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AmenitiesReservationsListComponent, FakeTranslatePipe],
      providers: [
        { provide: BookingService, useValue: mockBookingService },
        { provide: HateoasLinksService, useValue: mockLinkService },
        { provide: ToastService, useValue: mockToastService },
        { provide: ConfirmationService, useValue: mockConfirmationService },
        { provide: TranslateService, useValue: mockTranslateService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: Router, useValue: mockRouter },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(AmenitiesReservationsListComponent);
    component = fixture.componentInstance;
  });

  describe('Initialization and loadReservations', () => {
    it('should load reservations on init with query param reservationsPage = 1', () => {
      // Set up the bookingService.getBookings to return dummy data.
      const dummyResponse = {
        bookings: [
          {
            self: 'b1',
            bookingDate: '2020-01-01',
            amenity: {
              name: 'Test Amenity',
              description: 'desc',
              availableShifts: [],
              self: 'amenity_self',
            },
            shift: {
              startTime: '08:00:00',
              endTime: '09:00:00',
              day: '2020-01-01',
              taken: false,
              self: 'shift1',
            },
          },
        ],
        totalPages: 3,
      };
      mockBookingService.getBookings.and.returnValue(of(dummyResponse));

      fixture.detectChanges(); // triggers ngOnInit

      expect(component.currentPage).toEqual(1);
      expect(mockLinkService.getLink).toHaveBeenCalledWith(
        LinkKey.NEIGHBORHOOD_BOOKINGS,
      );
      expect(mockLinkService.getLink).toHaveBeenCalledWith(LinkKey.USER_SELF);

      expect(mockBookingService.getBookings).toHaveBeenCalledWith(
        'bookings_url',
        {
          page: 1,
          size: component.pageSize,
          bookedBy: 'user_self_link',
        },
      );

      // Check that the component updates the reservations list and pagination.
      expect(component.reservationsList).toEqual(dummyResponse.bookings);
      expect(component.totalPages).toEqual(dummyResponse.totalPages);
      expect(component.isLoading).toBeFalse();
    });

    it('should handle error when loading reservations', () => {
      // Set getBookings to return an error.
      const errorResponse = new Error('error loading');
      mockBookingService.getBookings.and.returnValue(
        throwError(() => errorResponse),
      );

      fixture.detectChanges(); // triggers ngOnInit

      expect(component.isLoading).toBeFalse();
      expect(mockToastService.showToast).toHaveBeenCalledWith(
        'AMENITIES-RESERVATIONS-LIST.THERE_WAS_A_PROBLEM_GETTING_YOUR_RESERVATIONS',
        'error',
      );
      expect(component.showErrorMessage).toBeTrue();
    });
  });

  describe('Pagination', () => {
    beforeEach(() => {
      // Set a dummy success response for getBookings.
      const dummyResponse = { bookings: [{ self: 'b1' }], totalPages: 3 };
      mockBookingService.getBookings.and.returnValue(of(dummyResponse));
    });

    it('goToPage should update currentPage, update query params, and reload reservations', () => {
      component.totalPages = 3;
      component.goToPage(2);
      expect(component.currentPage).toEqual(2);
      expect(mockRouter.navigate).toHaveBeenCalledWith([], {
        relativeTo: mockActivatedRoute,
        queryParams: { reservationsPage: 2 },
        queryParamsHandling: 'merge',
      });
      expect(mockBookingService.getBookings).toHaveBeenCalled();
    });

    it('nextPage should go to next page if available', () => {
      component.totalPages = 3;
      component.currentPage = 1;
      component.nextPage();
      expect(component.currentPage).toEqual(2);
    });

    it('prevPage should go to previous page if available', () => {
      component.totalPages = 3;
      component.currentPage = 2;
      component.prevPage();
      expect(component.currentPage).toEqual(1);
    });

    it('goToPage should do nothing if page is out of range', () => {
      component.totalPages = 3;
      component.currentPage = 1;
      component.goToPage(0);
      expect(component.currentPage).toEqual(1);
      component.goToPage(4);
      expect(component.currentPage).toEqual(1);
    });
  });

  describe('deleteReservation', () => {
    const dummyBooking: Booking = {
      self: 'booking1',
      bookingDate: '2020-01-01',
      amenity: {
        name: 'Test Amenity',
        description: '',
        availableShifts: [],
        self: 'amenity_self',
      },
      shift: {
        startTime: '08:00:00',
        endTime: '09:00:00',
        day: '2020-01-01',
        taken: false,
        self: 'shift1',
      },
    };

    beforeEach(() => {
      // Assume reservationsList has the dummy booking.
      component.reservationsList = [
        dummyBooking,
        { ...dummyBooking, self: 'booking2' },
      ];
    });

    it('should delete a reservation if confirmed and show success toast', () => {
      // Simulate confirmation returning true.
      mockConfirmationService.askForConfirmation.and.returnValue(of(true));
      // Simulate a successful deletion.
      mockBookingService.deleteBooking.and.returnValue(of({}));
      // Also, simulate a reload response.
      const dummyResponse = { bookings: [{ self: 'booking2' }], totalPages: 1 };
      mockBookingService.getBookings.and.returnValue(of(dummyResponse));

      component.deleteReservation(dummyBooking);

      // Expect confirmation to have been asked.
      expect(mockConfirmationService.askForConfirmation).toHaveBeenCalled();

      // Expect deleteBooking to be called with the booking self URL.
      expect(mockBookingService.deleteBooking).toHaveBeenCalledWith(
        dummyBooking.self,
      );

      // After deletion, the reservation list should be filtered.
      // And loadReservations is called to reload the data.
      expect(mockBookingService.getBookings).toHaveBeenCalled();

      // And success toast is shown.
      expect(mockToastService.showToast).toHaveBeenCalledWith(
        'AMENITIES-RESERVATIONS-LIST.YOUR_RESERVATION_FOR_BOOKINGAMENITYNAME_ON_BOOKING',
        'success',
      );
    });

    it('should show error toast if deletion fails', () => {
      // Simulate confirmation returning true.
      mockConfirmationService.askForConfirmation.and.returnValue(of(true));
      // Simulate a deletion error.
      const errorResponse = new Error('deletion error');
      mockBookingService.deleteBooking.and.returnValue(
        throwError(() => errorResponse),
      );

      component.deleteReservation(dummyBooking);

      expect(mockBookingService.deleteBooking).toHaveBeenCalledWith(
        dummyBooking.self,
      );
      expect(mockToastService.showToast).toHaveBeenCalledWith(
        'AMENITIES-RESERVATIONS-LIST.WE_COULDNT_CANCEL_YOUR_RESERVATION_FOR_BOOKINGAMEN',
        'error',
      );
    });
  });
});
