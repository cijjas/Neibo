import { Component, OnInit } from '@angular/core';
import { BookingService, Booking, LinkKey } from '@shared/index';
import {
  ConfirmationService,
  HateoasLinksService,
  ToastService,
} from '@core/index';
import { ActivatedRoute, Router } from '@angular/router';

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

    private router: Router
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
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
        next: (data) => {
          this.reservationsList = data.bookings || []; // Ensure it's an array
          this.totalPages = data.totalPages || 1;
          this.isLoading = false; // Stop loading
        },
        error: (err) => {
          console.error(err);
          this.toastService.showToast(
            'There was a problem getting your reservations.',
            'error'
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
    this.isLoading = true;

    this.confirmationService
      .askForConfirmation({
        title: `Cancel Reservation`,
        message: `Are you sure you want to cancel your reservation for '${booking.amenity.name}' on ${booking.bookingDate}, starting at ${booking.shift.startTime}?`,
        confirmText: 'Yes, Cancel',
        cancelText: 'No, Keep',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.bookingService.deleteBooking(booking.self).subscribe({
            next: () => {
              this.reservationsList = this.reservationsList.filter(
                (reservation) => reservation.self !== booking.self
              );
              this.loadReservations(); // Reload reservations after deletion
              this.toastService.showToast(
                `Your reservation for '${booking.amenity.name}' on ${booking.bookingDate} from ${booking.shift.startTime} to ${booking.shift.endTime} has been successfully canceled.`,
                'success'
              );
            },
            error: (err) => {
              console.error(err);
              this.toastService.showToast(
                `We couldn't cancel your reservation for '${booking.amenity.name}' on ${booking.bookingDate} from ${booking.shift.startTime} to ${booking.shift.endTime}. Please check your connection or try again later.`,
                'error'
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
