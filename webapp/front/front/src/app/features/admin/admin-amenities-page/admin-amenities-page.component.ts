import { Component, OnInit } from '@angular/core';
import { Amenity, Shift, AmenityService, ShiftService } from '@shared/index';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ConfirmationService,
  HateoasLinksService,
  ToastService,
} from '@core/index';

@Component({
  selector: 'app-admin-amenities-page',
  templateUrl: './admin-amenities-page.component.html',
})
export class AdminAmenitiesPageComponent implements OnInit {
  amenities: Amenity[] = [];
  currentPage = 1;
  totalPages = 1;
  pageSize = 10; // Number of amenities per page

  isLoading = false;

  // Dynamically loaded from the backend
  allShifts: Shift[] = [];
  uniqueDays: string[] = [];
  uniqueTimes: string[] = [];

  // Optional day abbreviations if you want them
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
    private amenityService: AmenityService,
    private shiftService: ShiftService, // 1) Inject shift service
    private router: Router,
    private route: ActivatedRoute,
    private toastService: ToastService,
    private confirmationService: ConfirmationService,
    private linkService: HateoasLinksService
  ) {}

  ngOnInit(): void {
    // Get initial page/size from query params
    this.route.queryParams.subscribe((params) => {
      this.currentPage = +params['page'] || 1;
      this.pageSize = +params['size'] || 10;
      this.loadAmenities();
    });

    // Also load all shifts
    this.loadShifts();
  }

  /**
   * Fetch all possible shifts from the API, then generate uniqueDays/uniqueTimes.
   */
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
        console.error('Error loading shifts:', err);
      },
    });
  }

  loadAmenities(): void {
    if (this.isLoading) return;
    this.isLoading = true;

    this.amenityService
      .getAmenities({
        page: this.currentPage,
        size: this.pageSize,
      })
      .subscribe({
        next: (response) => {
          this.amenities = response.amenities;
          this.currentPage = response.currentPage;
          this.totalPages = response.totalPages;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading amenities:', err);
          this.isLoading = false;
        },
      });
  }

  deleteAmenity(amenity: Amenity) {
    this.confirmationService
      .askForConfirmation({
        title: `Delete Amenity '${amenity.name}'`,
        message: 'Are you sure you want to delete this amenity?',
        confirmText: 'Delete',
        cancelText: 'Cancel',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.amenityService.deleteAmenity(amenity.self).subscribe({
            next: () => {
              this.toastService.showToast(
                `Amenity '${amenity.name}' deleted successfully.`,
                'success'
              );
              this.loadAmenities();
            },
            error: (err) => {
              this.toastService.showToast(
                `Could not remove '${amenity.name}'. Try Again.`,
                'error'
              );
              console.error('Error deleting amenities:', err);
            },
          });
        }
      });
  }

  // Check if a given day/time slot is available for this amenity
  checkAvailability(shifts: Shift[], dayKey: string, timeKey: string): boolean {
    if (!shifts) return false;
    return shifts.some(
      (shift) => shift.day === dayKey && shift.startTime === timeKey
    );
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

  /**
   * (Optional) formatting for "HH:mm:ss" -> "HH:mm"
   */
  formatTime(time: string): string {
    const [hours, minutes] = time.split(':');
    return `${hours}:${minutes}`;
  }
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
