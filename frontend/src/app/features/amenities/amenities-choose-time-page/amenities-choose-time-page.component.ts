import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { UserSessionService, HateoasLinksService } from '@core/index';
import {
  Amenity,
  Shift,
  AmenityService,
  BookingService,
  ShiftService,
  LinkKey,
} from '@shared/index';
import { Subscription } from 'rxjs';
import { AppTitleKeys } from '@shared/constants/app-titles';
import { TranslateService } from '@ngx-translate/core';
import { Title } from '@angular/platform-browser';
import { enUS, es } from 'date-fns/locale';
import { format } from 'date-fns';

interface ShiftTime {
  start: string;
  end: string;
}

@Component({
  selector: 'app-amenities-choose-time-page',
  templateUrl: './amenities-choose-time-page.component.html',
})
export class AmenitiesChooseTimePageComponent implements OnInit, OnDestroy {
  amenityUrl!: string;
  date!: string;
  displayDate: string;
  amenityName: string = '';
  bookings: Shift[] = [];
  private queryParamsSubscription!: Subscription;
  // Remove the old string and use an array of shift times
  selectedShiftTimes: ShiftTime[] = [];
  isLoading = false;

  shiftsForm = this.fb.group({
    selectedShifts: this.fb.array([]), // will be populated dynamically
  });

  showReservationDialog: boolean = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private amenityService: AmenityService,
    private bookingService: BookingService,
    private shiftService: ShiftService,
    private linkService: HateoasLinksService,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    this.queryParamsSubscription = this.route.queryParamMap.subscribe(
      params => {
        this.amenityUrl = params.get('amenityUrl') || '';
        this.date = params.get('date') || '';
        const userLang = localStorage.getItem('language'); // Default to English
        const locale = userLang === 'es' ? es : enUS; // Select locale
        this.displayDate = format(this.date, 'EE, dd MMM yyyy', {
          locale,
        });
        if (this.amenityUrl && this.date) {
          this.loadData();
        }
      },
    );
  }

  loadData() {
    this.isLoading = true;

    this.amenityService.getAmenity(this.amenityUrl).subscribe({
      next: (amenity: Amenity) => {
        this.amenityName = amenity.name;
        this.translate
          .get(AppTitleKeys.AMENITIES_CHOOSE_TIME_PAGE, {
            amenityName: this.amenityName,
          })
          .subscribe((translatedTitle: string) => {
            this.titleService.setTitle(translatedTitle);
          });
      },
      error: err => {
        const title = this.translate.instant(
          AppTitleKeys.AMENITIES_CHOOSE_TIME_PAGE,
        );
        this.titleService.setTitle(title);
        console.error(err);
      },
    });
    this.shiftService
      .getShifts({ forAmenity: this.amenityUrl, forDate: this.date })
      .subscribe({
        next: (data: Shift[]) => {
          this.bookings = data;

          const formArray = this.shiftsForm.get('selectedShifts') as FormArray;
          formArray.clear();

          this.bookings.forEach(booking => {
            const control = this.fb.control({
              value: false,
              disabled: booking.taken,
            });
            formArray.push(control);
          });

          this.isLoading = false;
        },
        error: err => {
          console.error(err);
          this.isLoading = false;
        },
      });
  }

  goBack() {
    this.location.back();
  }

  onReserve() {
    const formArray = this.shiftsForm.get('selectedShifts') as FormArray;

    const selectedShifts: string[] = [];
    // Prepare an array for table data instead of HTML strings
    const shiftTimes: ShiftTime[] = [];
    formArray.controls.forEach((ctrl, i) => {
      if (ctrl.value === true && !this.bookings[i].taken) {
        selectedShifts.push(this.bookings[i].self);
        // Format time to remove seconds (hh:mm)
        const formattedStartTime = this.bookings[i].startTime.slice(0, 5);
        const formattedEndTime = this.bookings[i].endTime.slice(0, 5);
        shiftTimes.push({ start: formattedStartTime, end: formattedEndTime });
      }
    });

    let userUrl: string = this.linkService.getLink(LinkKey.USER_SELF);

    this.bookingService
      .createBooking(this.amenityUrl, this.date, selectedShifts, userUrl)
      .subscribe({
        next: () => {
          // Instead of joining HTML strings, store the array
          this.selectedShiftTimes = shiftTimes;
          this.showReservationDialog = true;
        },
        error: err => {
          console.error(err);
          this.router.navigate(['/not-found']); // Navigate to the error page
        },
      });
  }

  closeReservationDialog() {
    this.showReservationDialog = false;
  }

  redirectToReservations() {
    this.showReservationDialog = false;
    this.router.navigate(['/amenities']);
  }

  ngOnDestroy(): void {
    this.queryParamsSubscription.unsubscribe();
  }

  isReserveDisabled(): boolean {
    const formArray = this.shiftsForm.get('selectedShifts') as FormArray;
    return !formArray.controls.some(control => control.value === true);
  }
}
