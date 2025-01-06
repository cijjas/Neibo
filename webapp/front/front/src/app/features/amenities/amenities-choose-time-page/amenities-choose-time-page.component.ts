import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { UserSessionService, HateoasLinksService } from '@core/index';
import {
  Amenity,
  Shift,
  AmenityService,
  BookingService,
  ShiftService,
  LinkKey
} from '@shared/index';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-amenities-choose-time-page',
  templateUrl: './amenities-choose-time-page.component.html',
})
export class AmenitiesChooseTimePageComponent implements OnInit {
  darkMode: boolean = false; // or retrieve from a service or user settings

  amenityUrl!: string;
  date!: string;
  amenityName: string = '';
  bookings: Shift[] = [];
  private queryParamsSubscription!: Subscription;
  selectedShiftsPopup: string[] = [];
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
    private userSessionService: UserSessionService
  ) { }

  ngOnInit(): void {
    this.queryParamsSubscription = this.route.queryParamMap.subscribe(
      (params) => {
        this.amenityUrl = params.get('amenityUrl') || '';
        this.date = params.get('date') || '';
        if (this.amenityUrl && this.date) {
          this.loadData();
        }
      }
    );
  }

  loadData() {
    this.isLoading = true;

    this.amenityService.getAmenity(this.amenityUrl).subscribe({
      next: (amenity: Amenity) => {
        this.amenityName = amenity.name;
      },
      error: (err) => {
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

          this.bookings.forEach((booking) => {
            const control = this.fb.control({
              value: false,
              disabled: booking.taken,
            });
            formArray.push(control);
          });

          this.isLoading = false;
        },
        error: (err) => {
          console.error(err);
          this.isLoading = false;
        },
      });
  }

  goBack() {
    this.location.back();
  }

  formattedShiftTimes: string = '';

  onReserve() {
    const formArray = this.shiftsForm.get('selectedShifts') as FormArray;

    const selectedShifts: string[] = [];
    const selectedShiftTimes: string[] = [];
    formArray.controls.forEach((ctrl, i) => {
      if (ctrl.value === true && !this.bookings[i].taken) {
        selectedShifts.push(this.bookings[i].self);
        // Format time to remove seconds (hh:mm)
        const formattedStartTime = this.bookings[i].startTime.slice(0, 5);
        const formattedEndTime = this.bookings[i].endTime.slice(0, 5);
        selectedShiftTimes.push(
          `<p>${formattedStartTime} - ${formattedEndTime}</p>`
        );
      }
    });

    let userUrl: string = this.linkService.getLink(LinkKey.USER_SELF);

    this.bookingService
      .createBooking(this.amenityUrl, this.date, selectedShifts, userUrl)
      .subscribe({
        next: () => {
          this.formattedShiftTimes = selectedShiftTimes.join('<br>');
          this.showReservationDialog = true;
        },
        error: (err) => {
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
    return !formArray.controls.some((control) => control.value === true);
  }
}
