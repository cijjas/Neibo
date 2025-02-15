import { Component, OnInit } from '@angular/core';
import { BookingService, Booking, LinkKey } from '@shared/index';
import {
  ConfirmationService,
  HateoasLinksService,
  ToastService,
} from '@core/index';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-amenities-reservations-list',
  templateUrl: './amenities-reservations-list.component.html',
})
export class AmenitiesReservationsListComponent implements OnInit {
  isLoading: boolean = false;
  reservationsList: Booking[] = [];
  showErrorMessage: boolean = false;

  currentPage: number = 1; // Start at page 1
  totalPages: number = 1; // Default total pages
  pageSize: number = 10; // Number of reservations per page

  constructor(
    private bookingService: BookingService,
    private linkService: HateoasLinksService,
    private route: ActivatedRoute,
    private toastService: ToastService,
    private confirmationService: ConfirmationService,
    private translate: TranslateService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      // Use a unique query parameter for this widget
      this.currentPage = +params['reservationsPage'] || 1; // Default to page 1
      this.loadReservations();
    });
  }

  loadReservations(): void {
    this.isLoading = true;
    const bookingsUrl = this.linkService.getLink(LinkKey.NEIGHBORHOOD_BOOKINGS);

    this.bookingService
      .getBookings(bookingsUrl, {
        page: this.currentPage,
        size: this.pageSize,
        bookedBy: this.linkService.getLink(LinkKey.USER_SELF),
      })
      .subscribe({
        next: data => {
          this.reservationsList = data.bookings || []; // Ensure its an array
          this.totalPages = data.totalPages || 1;
          this.isLoading = false; // Stop loading
        },
        error: err => {
          console.error(err);
          this.toastService.showToast(
            this.translate.instant(
              'AMENITIES-RESERVATIONS-LIST.THERE_WAS_A_PROBLEM_GETTING_YOUR_RESERVATIONS',
            ),
            'error',
          );
          this.showErrorMessage = true;
          this.isLoading = false; // Stop loading on error
        },
      });
  }

  goToPage(page: number): void {
    if (page < 1 || page > this.totalPages) return;

    this.currentPage = page;
    this.updateQueryParams(); // Update URL query params
    this.loadReservations();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.goToPage(this.currentPage + 1);
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.goToPage(this.currentPage - 1);
    }
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { reservationsPage: this.currentPage },
      queryParamsHandling: 'merge', // Merge with other query params
    });
  }

  deleteReservation(booking: Booking): void {
    this.confirmationService
      .askForConfirmation({
        title: this.translate.instant(
          'AMENITIES-RESERVATIONS-LIST.CANCEL_RESERVATION',
        ),
        message: this.translate.instant(
          'AMENITIES-RESERVATIONS-LIST.ARE_YOU_SURE_YOU_WANT_TO_CANCEL_YOUR_RESERVATION_F',
          {
            bookingAmenityName: booking.amenity.name,
            bookingBookingDate: booking.bookingDate,
            bookingShiftStartTime: booking.shift.startTimeDisplay,
          },
        ),
        confirmText: this.translate.instant(
          'AMENITIES-RESERVATIONS-LIST.YES_CANCEL',
        ),
        cancelText: this.translate.instant(
          'AMENITIES-RESERVATIONS-LIST.NO_KEEP',
        ),
      })
      .subscribe(confirmed => {
        if (confirmed) {
          this.bookingService.deleteBooking(booking.self).subscribe({
            next: () => {
              this.reservationsList = this.reservationsList.filter(
                reservation => reservation.self !== booking.self,
              );
              this.loadReservations(); // Reload reservations after deletion
              this.toastService.showToast(
                this.translate.instant(
                  'AMENITIES-RESERVATIONS-LIST.YOUR_RESERVATION_FOR_BOOKINGAMENITYNAME_ON_BOOKING',
                  {
                    bookingAmenityName: booking.amenity.name,
                    bookingBookingDate: booking.bookingDate,
                    bookingShiftStartTime: booking.shift.startTime,
                    bookingShiftEndTime: booking.shift.endTime,
                  },
                ),
                'success',
              );
            },
            error: err => {
              console.error(err);
              this.toastService.showToast(
                this.translate.instant(
                  'AMENITIES-RESERVATIONS-LIST.WE_COULDNT_CANCEL_YOUR_RESERVATION_FOR_BOOKINGAMEN',
                  {
                    bookingAmenityName: booking.amenity.name,
                    bookingBookingDate: booking.bookingDate,
                    bookingShiftStartTime: booking.shift.startTime,
                    bookingShiftEndTime: booking.shift.endTime,
                  },
                ),
                'error',
              );
            },
            complete: () => {
              this.isLoading = false;
            },
          });
        }
      });
  }
}
