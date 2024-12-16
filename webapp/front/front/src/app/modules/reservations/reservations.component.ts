import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AmenityService, ShiftService, BookingService, HateoasLinksService } from '../../shared/services/index.service'; // Adjust path as needed
import { Booking, Amenity } from '../../shared/models';

@Component({
  selector: 'app-reservations',
  templateUrl: './reservations.component.html',
})
export class ReservationsComponent implements OnInit {
  darkMode: Boolean = false; // Set or retrieve based on user preference
  amenities: Amenity[] = [];
  reservationsList: Booking[] = [];
  reservationForm!: FormGroup;

  showSuccessMessage = false;
  showErrorMessage = false;

  // If pagination is needed later:
  currentPage = 1;
  totalPages = 1;

  constructor(
    private fb: FormBuilder,
    private amenityService: AmenityService,
    private linkService: HateoasLinksService,
  ) { }

  ngOnInit(): void {
    this.reservationForm = this.fb.group({
      amenityId: ['', Validators.required],
      date: ['', Validators.required]
    });

    this.loadAmenities();
    this.linkService.logLinks()
    console.log(this.amenities);

    this.loadReservations();
  }

  loadAmenities(): void {
    const amenitiesUrl = this.linkService.getLink('neighborhood:amenities');
    this.amenityService.getAmenities(amenitiesUrl).subscribe({
      next: (data) => {
        this.amenities = data.amenities;
        this.currentPage = data.currentPage;
        this.totalPages = data.totalPages;
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  loadReservations(): void {
    // Load reservations here from your service
    // Example:
    // this.reservationService.getReservations().subscribe(res => this.reservationsList = res);
  }

  onSubmit(): void {
    if (this.reservationForm.valid) {
      // Submit form logic:
      // For example, call a service to see times or create a reservation
      // If success:
      // this.showSuccessMessage = true;
      // If error:
      // this.showErrorMessage = true;
    }
  }

  deleteReservation(bookingIds: string): void {
    // Implement reservation deletion logic here.
    // After success:
    // Refresh the reservations list.
    // Example:
    // this.reservationService.deleteReservation(bookingIds).subscribe(() => this.loadReservations());
  }

}
