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
import { catchError, map, Observable, of, switchMap } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-amenities-reservations-page',
  templateUrl: './amenities-reservations-page.component.html',
})
export class AmenitiesReservationsPageComponent implements OnInit {
  amenities: Amenity[] = [];
  reservationsList: Booking[] = [];
  reservationForm!: FormGroup;

  currentPage = 1; // Current or highest loaded page
  totalPages = 1;
  pageSize = 10;
  isLoading = false;

  // For dynamic day/time
  allShifts: Shift[] = [];
  uniqueDays: string[] = [];
  uniqueTimes: string[] = [];

  // (Optional) day name abbreviations
  private dayAbbreviations: Record<string, string> = {
    Monday: this.translate.instant('AMENITIES-RESERVATIONS-PAGE-PAGE.MON'),
    Tuesday: this.translate.instant('AMENITIES-RESERVATIONS-PAGE.TUE'),
    Wednesday: this.translate.instant('AMENITIES-RESERVATIONS-PAGE.WED'),
    Thursday: this.translate.instant('AMENITIES-RESERVATIONS-PAGE.THU'),
    Friday: this.translate.instant('AMENITIES-RESERVATIONS-PAGE.FRI'),
    Saturday: this.translate.instant('AMENITIES-RESERVATIONS-PAGE.SAT'),
    Sunday: this.translate.instant('AMENITIES-RESERVATIONS-PAGE.SUN'),
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
    private shiftService: ShiftService,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.reservationForm = this.fb.group({
      amenity: ['', Validators.required],
      date: ['', Validators.required],
    });

    // Load the first page + shifts
    this.loadShifts();

    this.route.queryParams
      .pipe(
        switchMap((params) => {
          this.currentPage = +params['page'] || 1;
          this.pageSize = +params['size'] || 10;

          // Update queryParams if defaults are missing
          const missingParams: any = {};
          if (!params['page']) missingParams['page'] = this.currentPage;
          if (!params['size']) missingParams['size'] = this.pageSize;

          if (Object.keys(missingParams).length > 0) {
            this.router.navigate([], {
              relativeTo: this.route,
              queryParams: { ...missingParams },
              queryParamsHandling: 'merge',
            });
          }

          return this.loadAmenities();
        })
      )
      .subscribe();
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
  private loadAmenities(): Observable<void> {
    const queryParams = {
      page: this.currentPage,
      size: this.pageSize,
    };

    return this.amenityService.getAmenities(queryParams).pipe(
      map((response) => {
        if (response) {
          this.amenities = response.amenities;
          this.totalPages = response.totalPages;
          this.currentPage = response.currentPage;
        } else {
          this.amenities = [];
          this.totalPages = 0;
        }
        this.isLoading = false;
      }),
      catchError((error) => {
        console.error('Error loading amenities:', error);
        this.amenities = [];
        this.totalPages = 0;
        this.isLoading = false;
        return of();
      })
    );
  }

  onPageChange(page: number): void {
    if (page < 1 || page > this.totalPages || page === this.currentPage) {
      return;
    }
    this.currentPage = page;
    this.updateQueryParams();
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.currentPage, size: this.pageSize },
      queryParamsHandling: 'merge',
    });
  }

  // Reservation form submission
  onSubmit(): void {
    if (this.reservationForm.valid) {
      const amenityUrl = this.reservationForm.get('amenity')?.value.self;
      const date = this.reservationForm.get('date')?.value;
      this.router.navigate(['/amenities/choose-time'], {
        queryParams: { amenityUrl, date },
      });
    } else {
      this.reservationForm.markAllAsTouched();
    }
  }

  selectAmenity(amenitySelfLink: string) {
    this.reservationForm.get('amenity')?.setValue(amenitySelfLink);
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

  fetchAmenities = (page: number, size: number): Observable<any> => {
    return this.amenityService.getAmenities({ page, size }).pipe(
      map((response) => ({
        items: response.amenities,
        currentPage: response.currentPage,
        totalPages: response.totalPages,
      }))
    );
  };

  displayAmenity = (amenity: Amenity): string => {
    return amenity.name;
  };
}

/** Utility sorting functions */
function sortDays(a: string, b: string) {
  const order = [
    this.translate.instant('AMENITIES-RESERVATIONS-PAGE-PAGE.MONDAY'),
    this.translate.instant('AMENITIES-RESERVATIONS-PAGE-PAGE.TUESDAY'),
    this.translate.instant('AMENITIES-RESERVATIONS-PAGE-PAGE.WEDNESDAY'),
    this.translate.instant('AMENITIES-RESERVATIONS-PAGE-PAGE.THURSDAY'),
    this.translate.instant('AMENITIES-RESERVATIONS-PAGE-PAGE.FRIDAY'),
    this.translate.instant('AMENITIES-RESERVATIONS-PAGE-PAGE.SATURDAY'),
    this.translate.instant('AMENITIES-RESERVATIONS-PAGE-PAGE.SUNDAY'),
  ];
  return order.indexOf(a) - order.indexOf(b);
}

function sortTimes(a: string, b: string) {
  const aH = parseInt(a.split(':')[0], 10);
  const bH = parseInt(b.split(':')[0], 10);
  return aH - bH;
}
