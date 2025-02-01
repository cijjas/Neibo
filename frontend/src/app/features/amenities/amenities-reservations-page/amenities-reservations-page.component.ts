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

  currentPage = 1;
  totalPages = 1;
  pageSize = 10;
  isLoading = false;

  // For dynamic day/time
  allShifts: Shift[] = [];
  uniqueDays: string[] = [];
  uniqueTimes: string[] = [];

  private dayAbbreviations: Record<string, string> = {
    Monday: this.translate.instant('ADMIN-AMENITIES-PAGE.MON'),
    Tuesday: this.translate.instant('ADMIN-AMENITIES-PAGE.TUE'),
    Wednesday: this.translate.instant('ADMIN-AMENITIES-PAGE.WED'),
    Thursday: this.translate.instant('ADMIN-AMENITIES-PAGE.THU'),
    Friday: this.translate.instant('ADMIN-AMENITIES-PAGE.FRI'),
    Saturday: this.translate.instant('ADMIN-AMENITIES-PAGE.SAT'),
    Sunday: this.translate.instant('ADMIN-AMENITIES-PAGE.SUN'),
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
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.reservationForm = this.fb.group({
      amenity: ['', Validators.required],
      date: ['', Validators.required],
    });

    // Get initial page/size from query params
    this.route.queryParams.subscribe(params => {
      this.currentPage = +params['page'] || 1;
      this.pageSize = +params['size'] || 10;
      this.loadAmenities();
    });

    // Also load all shifts
    this.loadShifts();
  }

  // Example shift-loading logic
  private loadShifts(): void {
    this.shiftService.getShifts().subscribe({
      next: shiftsFromApi => {
        this.allShifts = shiftsFromApi;
        const daysSet = new Set<string>();
        const timesSet = new Set<string>();

        for (const shift of this.allShifts) {
          daysSet.add(shift.day);
          timesSet.add(shift.startTime);
        }

        // LISTA
        this.uniqueDays = Array.from(daysSet).sort(sortDays);
        console.log('hola');
        this.uniqueTimes = Array.from(timesSet).sort(sortTimes);
      },
      error: err => {
        console.error('Error fetching shifts:', err);
      },
    });
  }

  // Unified loader

  loadAmenities(): void {
    if (this.isLoading) return;
    this.isLoading = true;

    this.amenityService
      .getAmenities({
        page: this.currentPage,
        size: this.pageSize,
      })
      .subscribe({
        next: response => {
          this.amenities = response.amenities;
          this.currentPage = response.currentPage;
          this.totalPages = response.totalPages;
          this.isLoading = false;
        },
        error: err => {
          console.error('Error loading amenities:', err);
          this.isLoading = false;
        },
      });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.updateQueryParams();
    this.loadAmenities();
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
      this.router.navigate(['/amenities', 'choose-time'], {
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
      shift => shift.day === dayKey && shift.startTime === timeKey,
    );
  }

  // Format "HH:mm:ss" -> "HH:mm"
  formatTime(time: string): string {
    const [hours, minutes] = time.split(':');
    return `${hours}:${minutes}`;
  }

  fetchAmenities = (page: number, size: number): Observable<any> => {
    return this.amenityService.getAmenities({ page, size }).pipe(
      map(response => ({
        items: response.amenities,
        currentPage: response.currentPage,
        totalPages: response.totalPages,
      })),
    );
  };

  displayAmenity = (amenity: Amenity): string => {
    return amenity.name;
  };
}

/**
 * Example sort for days – so they appear Monday, Tuesday, etc.
 */
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

/**
 * Example sort for times as strings "HH:mm:ss".
 */
function sortTimes(a: string, b: string) {
  const aH = parseInt(a.split(':')[0], 10);
  const bH = parseInt(b.split(':')[0], 10);
  return aH - bH;
}
