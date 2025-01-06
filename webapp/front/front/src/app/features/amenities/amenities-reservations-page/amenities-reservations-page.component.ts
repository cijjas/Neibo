import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HateoasLinksService } from '@core/index';
import {
  Booking,
  Amenity,
  Shift,
  AmenityService,
  BookingService,
  ShiftService,
} from '@shared/index';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-amenities-reservations-page',
  templateUrl: './amenities-reservations-page.component.html',
})
export class AmenitiesReservationsPageComponent implements OnInit {
  darkMode = false;

  amenities: Amenity[] = [];
  reservationsList: Booking[] = [];
  reservationForm!: FormGroup;

  currentPage = 1; // Current or highest loaded page
  totalPages = 1;
  isLoading = false;

  // For dynamic day/time
  allShifts: Shift[] = [];
  uniqueDays: string[] = [];
  uniqueTimes: string[] = [];

  // (Optional) day name abbreviations
  private dayAbbreviations: Record<string, string> = {
    Monday: 'Mon',
    Tuesday: 'Tue',
    Wednesday: 'Wed',
    Thursday: 'Thu',
    Friday: 'Fri',
    Saturday: 'Sat',
    Sunday: 'Sun',
  };

  getAbbreviatedDay(day: string): string {
    return this.dayAbbreviations[day] || day;
  }

  constructor(
    private fb: FormBuilder,
    private amenityService: AmenityService,
    private linkService: HateoasLinksService,
    private bookingService: BookingService,
    private router: Router,
    private route: ActivatedRoute,
    private shiftService: ShiftService
  ) { }

  ngOnInit(): void {
    this.reservationForm = this.fb.group({
      amenityUrl: ['', Validators.required],
      date: ['', Validators.required],
    });

    // Load the first page + shifts
    this.loadAmenities(this.currentPage);
    this.loadShifts();
  }

  // Example shift-loading logic
  private loadShifts(): void {
    this.shiftService.getShifts().subscribe({
      next: (shiftsFromApi) => {
        this.allShifts = shiftsFromApi;
        const daysSet = new Set<string>();
        const timesSet = new Set<string>();

        for (const shift of this.allShifts) {
          daysSet.add(shift.day);
          timesSet.add(shift.startTime);
        }

        this.uniqueDays = Array.from(daysSet).sort(sortDays);
        this.uniqueTimes = Array.from(timesSet).sort(sortTimes);
      },
      error: (err) => {
        console.error('Error fetching shifts:', err);
      },
    });
  }

  // Unified loader
  loadAmenities(page: number): void {
    // If we’re already loading or page is invalid, do nothing
    if (this.isLoading || page < 1 || page > this.totalPages) {
      return;
    }

    this.amenityService.getAmenities({ page }).subscribe({
      next: (data) => {
        /**
         * If `page === 1`, we're refreshing from scratch.
         * Or you might want to always push if you want to keep old pages.
         */
        if (page === 1) {
          this.amenities = data.amenities;
        } else {
          // Append new data
          this.amenities = [...this.amenities, ...data.amenities];
        }

        // Update page tracking
        this.currentPage = data.currentPage;
        this.totalPages = data.totalPages;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
      },
    });
  }

  // When the user scrolls near the bottom in the Amenity selector
  onScroll(event: Event): void {
    // If already loading or on the last page, do nothing
    if (this.isLoading || this.currentPage >= this.totalPages) return;

    const target = event.target as HTMLElement;
    const threshold = 100;

    if (
      target.scrollHeight - target.scrollTop - target.clientHeight <
      threshold
    ) {
      // Load next page
      this.loadAmenities(this.currentPage + 1);
    }
  }

  // When the user selects a page from the paginator
  onPageChange(pageNumber: number): void {
    // If the user picks the same page or something invalid, skip
    if (
      pageNumber < 1 ||
      pageNumber > this.totalPages ||
      pageNumber === this.currentPage
    ) {
      return;
    }

    // Set the loading state
    this.isLoading = true;

    // Fetch the selected page and replace the amenities array
    this.amenityService.getAmenities({ page: pageNumber }).subscribe({
      next: (data) => {
        this.amenities = data.amenities; // Replace the previous page's data
        this.currentPage = pageNumber; // Update the current page
        this.isLoading = false; // Reset the loading state
      },
      error: (err) => {
        console.error('Error fetching amenities:', err);
        this.isLoading = false; // Reset the loading state even on error
      },
    });

    // Optionally, reflect the new page in the URL
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: pageNumber },
      queryParamsHandling: 'merge',
    });
  }

  // Reservation form submission
  onSubmit(): void {
    if (this.reservationForm.valid) {
      const amenityUrl = this.reservationForm.get('amenityUrl')?.value;
      const date = this.reservationForm.get('date')?.value;
      this.router.navigate(['/amenities/choose-time'], {
        queryParams: { amenityUrl, date },
      });
    } else {
      this.reservationForm.markAllAsTouched();
    }
  }

  selectAmenity(amenitySelfLink: string) {
    this.reservationForm.get('amenityUrl')?.setValue(amenitySelfLink);
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

  // Check if day/time is available
  checkAvailability(shifts: Shift[], dayKey: string, timeKey: string): boolean {
    if (!shifts) return false;
    return shifts.some(
      (shift) => shift.day === dayKey && shift.startTime === timeKey
    );
  }

  // Format "HH:mm:ss" -> "HH:mm"
  formatTime(time: string): string {
    const [hours, minutes] = time.split(':');
    return `${hours}:${minutes}`;
  }
}

/** Utility sorting functions */
function sortDays(a: string, b: string) {
  const order = [
    'Monday',
    'Tuesday',
    'Wednesday',
    'Thursday',
    'Friday',
    'Saturday',
    'Sunday',
  ];
  return order.indexOf(a) - order.indexOf(b);
}

function sortTimes(a: string, b: string) {
  const aH = parseInt(a.split(':')[0], 10);
  const bH = parseInt(b.split(':')[0], 10);
  return aH - bH;
}
