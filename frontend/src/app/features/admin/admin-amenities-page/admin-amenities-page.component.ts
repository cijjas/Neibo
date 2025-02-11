import { Component, OnInit } from '@angular/core';
import { Amenity, Shift, AmenityService, ShiftService } from '@shared/index';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ConfirmationService,
  HateoasLinksService,
  ToastService,
} from '@core/index';

import { TranslateService } from '@ngx-translate/core';
import { Title } from '@angular/platform-browser';
import { AppTitleKeys } from '@shared/constants/app-titles';
import { encodeUrlSafeBase64 } from '@shared/utils/url-safe-base64.util';

@Component({
  selector: 'app-admin-amenities-page',
  templateUrl: './admin-amenities-page.component.html',
})
export class AdminAmenitiesPageComponent implements OnInit {
  encodeUrlSafeBase64 = encodeUrlSafeBase64;

  amenities: Amenity[] = [];
  currentPage = 1;
  totalPages = 1;
  pageSize = 10; // Number of amenities per page

  isLoading = false;

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
    private amenityService: AmenityService,
    private shiftService: ShiftService,
    private router: Router,
    private route: ActivatedRoute,
    private toastService: ToastService,
    private confirmationService: ConfirmationService,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    const title = this.translate.instant(AppTitleKeys.ADMIN_AMENITIES_PAGE);
    this.titleService.setTitle(title);

    // Get initial page/size from query params
    this.route.queryParams.subscribe(params => {
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
  loadShifts(): void {
    this.shiftService.getShifts().subscribe({
      next: shiftsFromApi => {
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
      error: err => {
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

  deleteAmenity(amenity: Amenity) {
    const title = this.translate.instant(
      'ADMIN-AMENITIES-PAGE.DELETE_AMENITY_AMENITYNAME',
      { amenityName: amenity.name },
    );

    this.confirmationService
      .askForConfirmation({
        title: title,
        message: this.translate.instant(
          'ADMIN-AMENITIES-PAGE.ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_AMENITY',
        ),
        confirmText: this.translate.instant('ADMIN-AMENITIES-PAGE.DELETE'),
        cancelText: this.translate.instant('ADMIN-AMENITIES-PAGE.CANCEL'),
      })
      .subscribe(confirmed => {
        if (!confirmed) return;

        this.amenityService.deleteAmenity(amenity.self).subscribe({
          next: () => {
            this.toastService.showToast(
              this.translate.instant(
                'ADMIN-AMENITIES-PAGE.AMENITY_AMENITYNAME_DELETED_SUCCESSFULLY',
                {
                  amenityName: amenity.name,
                },
              ),
              'success',
            );

            this.amenities = this.amenities.filter(
              a => a.self !== amenity.self,
            );

            if (this.amenities.length === 0 && this.currentPage > 1) {
              this.currentPage--;
              this.updateQueryParams();
            }

            this.loadAmenities();
          },
          error: err => {
            this.toastService.showToast(
              this.translate.instant(
                'ADMIN-AMENITIES-PAGE.COULD_NOT_REMOVE_AMENITYNAME_TRY_AGAIN',
                {
                  amenityName: amenity.name,
                },
              ),
              'error',
            );
            console.error('Error deleting amenities:', err);
          },
        });
      });
  }

  // Check if a given day/time slot is available for this amenity
  checkAvailability(shifts: Shift[], dayKey: string, timeKey: string): boolean {
    if (!shifts) return false;
    return shifts.some(
      shift => shift.day === dayKey && shift.startTime === timeKey,
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

  formatTime(time: string): string {
    const [hours, minutes] = time.split(':');
    return `${hours}:${minutes}`;
  }
}

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
