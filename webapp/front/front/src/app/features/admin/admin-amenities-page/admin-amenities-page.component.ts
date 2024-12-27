import { Component, OnInit } from '@angular/core';
import { Amenity, Shift, AmenityService } from '@shared/index';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-admin-amenities-page',
  templateUrl: './admin-amenities-page.component.html',
})
export class AdminAmenitiesPageComponent implements OnInit {
  darkMode: Boolean = false;
  amenities: Amenity[] = [];
  currentPage = 1;
  totalPages = 1;
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
    { key: '09:00:00', label: '09:00 - 10:00' },
    { key: '10:00:00', label: '10:00 - 11:00' },
    { key: '11:00:00', label: '11:00 - 12:00' },
    { key: '12:00:00', label: '12:00 - 13:00' },
    { key: '13:00:00', label: '13:00 - 14:00' },
    { key: '14:00:00', label: '14:00 - 15:00' },
    { key: '15:00:00', label: '15:00 - 16:00' },
    { key: '16:00:00', label: '16:00 - 17:00' },
  ];

  constructor(
    private amenityService: AmenityService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.loadAmenities(this.currentPage);
  }

  loadAmenities(page: number): void {
    if (this.isLoading) return;
    this.isLoading = true;

    // this.amenityService.getAmenities({ page }).subscribe({
    //   next: (response) => {
    //     this.amenities = response.amenities;
    //     this.currentPage = response.currentPage;
    //     this.totalPages = response.totalPages;
    //     this.isLoading = false;
    //   },
    //   error: (err) => {
    //     console.error(err);
    //     this.isLoading = false;
    //   }
    // });
  }

  checkAvailability(shifts: Shift[], dayKey: string, timeKey: string): boolean {
    return shifts.some(shift => shift.day === dayKey && shift.startTime === timeKey);
  }

  onPageChange(pageNumber: number): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: pageNumber },
      queryParamsHandling: 'merge' // keep any existing query params
    });
    this.loadAmenities(pageNumber);
  }
}
