import { Component, OnInit } from '@angular/core';
import { Amenity, Shift, AmenityService } from '@shared/index';
import { ActivatedRoute, Router } from '@angular/router';
import { HateoasLinksService } from '@core/index';

@Component({
  selector: 'app-admin-amenities-page',
  templateUrl: './admin-amenities-page.component.html',
})
export class AdminAmenitiesPageComponent implements OnInit {
  darkMode: Boolean = false;
  amenities: Amenity[] = [];
  currentPage = 1;
  totalPages = 1;
  pageSize: number = 10; // Number of amenities per page

  isLoading = false;

  // Days and times (analogous to JSP)
  days = [
    { key: 'Monday', label: 'Mon' },
    { key: 'Tuesday', label: 'Tue' },
    { key: 'Wednesday', label: 'Wed' },
    { key: 'Thursday', label: 'Thu' },
    { key: 'Friday', label: 'Fri' },
    { key: 'Saturday', label: 'Sat' },
    { key: 'Sunday', label: 'Sun' }
  ];

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
    private amenityService: AmenityService,
    private router: Router,
    private route: ActivatedRoute,
    private linkService: HateoasLinksService
  ) { }

  ngOnInit(): void {
    // Get initial page and size from query params
    this.route.queryParams.subscribe((params) => {
      this.currentPage = +params['page'] || 1;
      this.pageSize = +params['size'] || 10;
      this.loadAmenities();
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

  deleteAmenity(amenityUrl: string) {
    this.amenityService.deleteAmenity(amenityUrl).subscribe({
      next: (response) => {
        // success toast
      },
      error: (err) => {
        console.error('Error deleting amenities:', err);
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

  checkAvailability(shifts: Shift[], dayKey: string, timeKey: string): boolean {
    return shifts.some(shift => shift.day === dayKey && shift.startTime === timeKey);
  }
}
