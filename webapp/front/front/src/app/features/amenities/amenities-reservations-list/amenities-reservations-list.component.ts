import { Component, OnInit } from '@angular/core';
import { BookingService, Booking, LinkKey } from '@shared/index';
import { HateoasLinksService, ToastService } from '@core/index';
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
    private router: Router
  ) {}

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

    this.bookingService.deleteBooking(booking.self).subscribe({
      next: () => {
        this.reservationsList = this.reservationsList.filter(
          (reservation) => reservation.self !== booking.self
        );
        this.loadReservations(); // Reload reservations after deletion
        this.toastService.showToast(
          `Reservation for ${booking.amenity.name} canceled.`,
          'success'
        );
      },
      error: (err) => {
        console.error(err);
        this.toastService.showToast(
          `Reservation for ${booking.amenity.name} could not be canceled. Try again.`,
          'error'
        );
        this.isLoading = false;
      },
    });
  }
}
