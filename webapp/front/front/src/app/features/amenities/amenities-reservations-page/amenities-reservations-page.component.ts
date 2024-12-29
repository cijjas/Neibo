import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HateoasLinksService } from '@core/index';
import { Booking, Amenity, Shift, AmenityService, BookingService } from '@shared/index';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
    selector: 'app-amenities-reservations-page',
    templateUrl: './amenities-reservations-page.component.html',
})
export class AmenitiesReservationsPageComponent implements OnInit {
    darkMode: Boolean = false;
    amenities: Amenity[] = [];
    reservationsList: Booking[] = [];
    reservationForm!: FormGroup;

    showSuccessMessage = false;
    showErrorMessage = false;

    currentPage = 1;
    totalPages = 1;
    isLoading = false;

    // Example static day and time definitions:
    // daysPairs and timesPairs are analogous to those in JSP
    days = [
        { key: 'Monday', label: 'Mon' },
        { key: 'Tuesday', label: 'Tue' },
        { key: 'Wednesday', label: 'Wed' },
        { key: 'Thursday', label: 'Thu' },
        { key: 'Friday', label: 'Fri' },
        { key: 'Saturday', label: 'Sat' },
        { key: 'Sunday', label: 'Sun' }
    ];

    // times could be in 24hr format or any format you like;
    // the key is the start time, and you can display a label if you wish.
    // For example, times every hour from 08:00 to 20:00:
    times = [
        { key: '00:00:00', label: '00:00 - 01:00' },
        { key: '01:00:00', label: '01:00 - 02:00' },
        { key: '02:00:00', label: '02:00 - 03:00' },
        { key: '03:00:00', label: '03:00 - 04:00' },
        { key: '04:00:00', label: '04:00 - 05:00' },
        { key: '05:00:00', label: '05:00 - 06:00' },
        { key: '06:00:00', label: '06:00 - 07:00' },
        { key: '07:00:00', label: '07:00 - 08:00' },
        { key: '08:00:00', label: '08:00 - 09:00' },
        { key: '09:00:00', label: '09:00 - 10:00' },
        { key: '10:00:00', label: '10:00 - 11:00' },
        { key: '11:00:00', label: '11:00 - 12:00' },
        { key: '12:00:00', label: '12:00 - 13:00' },
        { key: '13:00:00', label: '13:00 - 14:00' },
        { key: '14:00:00', label: '14:00 - 15:00' },
        { key: '15:00:00', label: '15:00 - 16:00' },
        { key: '16:00:00', label: '16:00 - 17:00' },
        { key: '17:00:00', label: '17:00 - 18:00' },
        { key: '18:00:00', label: '18:00 - 19:00' },
        { key: '19:00:00', label: '19:00 - 20:00' },
        { key: '20:00:00', label: '20:00 - 21:00' },
        { key: '21:00:00', label: '21:00 - 22:00' },
        { key: '22:00:00', label: '22:00 - 23:00' },
        { key: '23:00:00', label: '23:00 - 24:00' },

    ];

    constructor(
        private fb: FormBuilder,
        private amenityService: AmenityService,
        private linkService: HateoasLinksService,
        private bookingService: BookingService,
        private router: Router,
        private route: ActivatedRoute
    ) { }

    ngOnInit(): void {
        this.reservationForm = this.fb.group({
            amenityUrl: ['', Validators.required],
            date: ['', Validators.required]
        });

        this.loadAmenities(this.currentPage);
    }

    loadAmenities(page: number): void {
        if (this.isLoading) return;
        this.isLoading = true;

        const amenitiesUrl = this.linkService.getLink('neighborhood:amenities');
        this.amenityService.getAmenities({ page }).subscribe({
            next: (data) => {
                if (page === 1) {
                    this.amenities = data.amenities;
                } else {
                    this.amenities = [...this.amenities, ...data.amenities];
                }
                this.currentPage = data.currentPage;
                this.totalPages = data.totalPages;
                this.isLoading = false;
            },
            error: (err) => {
                console.error(err);
                this.isLoading = false;
            }
        });
    }



    onSubmit(): void {
        if (this.reservationForm.valid) {
            const amenityUrl = this.reservationForm.get('amenityUrl')?.value;
            const date = this.reservationForm.get('date')?.value;

            // Navigate to the Choose Time page with query parameters
            this.router.navigate(['/amenities/choose-time'], {
                queryParams: { amenityUrl, date }
            });
        } else {
            // Mark all controls as touched to trigger validation messages
            this.reservationForm.markAllAsTouched();
        }
    }


    selectAmenity(amenitySelfLink: string) {
        this.reservationForm.get('amenityUrl')?.setValue(amenitySelfLink);
    }

    onScroll(event: Event): void {
        const target = event.target as HTMLElement;
        const threshold = 100;
        if (
            !this.isLoading &&
            this.currentPage < this.totalPages &&
            target.scrollHeight - target.scrollTop - target.clientHeight < threshold
        ) {
            this.loadAmenities(this.currentPage + 1);
        }
    }


    deleteReservation(bookingUrl: string): void {
        this.isLoading = true;

        this.bookingService.deleteBooking(bookingUrl).subscribe({
            next: () => {
                this.reservationsList = this.reservationsList.filter(reservation => reservation.self !== bookingUrl);
                this.isLoading = false;
                this.showSuccessMessage = true;
            },
            error: (err) => {
                console.error(err);
                this.showErrorMessage = true;
                this.isLoading = false;
            }
        });
    }

    // Checks if a given day/time slot is available for this amenity
    checkAvailability(shifts: Shift[], dayKey: string, timeKey: string): boolean {
        return shifts.some(shift => shift.day === dayKey && shift.startTime === timeKey);
    }


    // Handle page changes from the paginator
    onPageChange(pageNumber: number): void {
        // Update the query param to reflect the new page
        this.router.navigate([], {
            relativeTo: this.route,
            queryParams: { page: pageNumber },
            queryParamsHandling: 'merge' // keep any other existing query params
        });
    }
}
