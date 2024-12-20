import { Component, OnInit } from '@angular/core';
import { BookingService, HateoasLinksService } from '../../shared/services/index.service';
import { Booking } from '../../shared/models';

@Component({
  selector: 'app-reservations-list',
  templateUrl: './reservations-list.component.html',
})
export class ReservationsListComponent implements OnInit {
  isLoading: boolean = false;
  reservationsList: Booking[] = [];
  showErrorMessage: boolean = false;

  constructor(
    private bookingService: BookingService,
    private linkService: HateoasLinksService
  ) { }

  ngOnInit(): void {
    this.loadReservations();
  }

  loadReservations(): void {
    this.isLoading = true;
    const bookingsUrl = this.linkService.getLink('neighborhood:bookings');

    this.bookingService.getBookings(bookingsUrl, {
      bookedBy: this.linkService.getLink('user:self'),
    }).subscribe({
      next: (data) => {
        this.reservationsList = data.bookings; // Assuming 'bookings' is the array in the response
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.showErrorMessage = true;
        this.isLoading = false;
      },
    });
  }

  deleteReservation(bookingUrl: string): void {
    this.isLoading = true;

    this.bookingService.deleteBooking(bookingUrl).subscribe({
      next: () => {
        this.reservationsList = this.reservationsList.filter(
          (reservation) => reservation.self !== bookingUrl
        );
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
      },
    });
  }
}
