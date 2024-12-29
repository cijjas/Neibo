import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastService } from '@core/index';
import { ShiftService, Shift, AmenityService } from '@shared/index';

@Component({
  selector: 'app-admin-amenity-edit-page',
  templateUrl: './admin-amenity-edit-page.component.html',
})
export class AdminAmenityEditPageComponent implements OnInit {
  amenityForm!: FormGroup;

  amenityName = '';

  allShifts: Shift[] = [];

  uniqueDays: string[] = [];
  uniqueTimes: string[] = [];

  // Shifts currently selected in the UI
  selectedShifts: Shift[] = [];
  amenityShiftRefs: string[] = [];


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
    return this.dayAbbreviations[day] || day; // Fallback to original name if not found
  }
  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private shiftService: ShiftService,
    private amenityService: AmenityService,
    private toastService: ToastService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Create the form
    this.amenityForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
    });

    // Get the ID from the URL
    const amenityId = this.route.snapshot.params['id'];

    // 1) Load the Amenity
    // We'll store the amenity’s shift references in some temporary variable
    // until we’ve loaded allShifts

    this.amenityService.getAmenity(amenityId).subscribe({
      next: (amenityData) => {
        // Fill name & desc
        this.amenityName = amenityData.name;
        this.amenityForm.patchValue({
          name: amenityData.name,
          description: amenityData.description,
        });

        this.amenityShiftRefs = amenityData.availableShifts.map((s) => s.self);
        // 2) Load all Shifts
        this.shiftService.getShifts().subscribe({
          next: (shiftsFromApi) => {
            this.allShifts = shiftsFromApi;

            // Build uniqueDays / uniqueTimes
            const daysSet = new Set<string>();
            const timesSet = new Set<string>();
            for (const shift of this.allShifts) {
              daysSet.add(shift.day);
              timesSet.add(shift.startTime);
            }
            this.uniqueDays = Array.from(daysSet).sort(sortDays);
            this.uniqueTimes = Array.from(timesSet).sort(sortTimes);


            // E.g., if amenityShiftRefs are shift 'self' URLs:
            if (this.amenityShiftRefs?.length) {
              this.selectedShifts = this.allShifts.filter((shift) =>
                this.amenityShiftRefs.includes(shift.self)
              );
            }


          },
          error: (err) => console.error(err),
        });


      },
      error: (err) => console.error('Error loading amenity:', err),
    });


  }

  get nameControl() {
    return this.amenityForm.get('name');
  }
  get descControl() {
    return this.amenityForm.get('description');
  }
  get anyShiftChecked(): boolean {
    return this.selectedShifts.length > 0;
  }

  onSubmit() {
    if (this.amenityForm.invalid || !this.anyShiftChecked) {
      this.amenityForm.markAllAsTouched();
      return;
    }

    const formValue = this.amenityForm.value;
    const amenityId = this.route.snapshot.params['id'];
    // Convert selectedShifts to an array of SHIFT URLs or SHIFT IDs
    const selectedShiftRefs: string[] = this.selectedShifts.map((s) => s.self);

    this.amenityService.updateAmenity(
      amenityId,
      formValue.name,
      formValue.description,
      selectedShiftRefs
    ).subscribe({
      next: (updatedAmenity) => {
        this.toastService.showToast('Amenity updated successfully!', 'success');
        this.router.navigate(['admin/amenities'])
      },
      error: (err) => {
        this.toastService.showToast('Error updating amenity, try again later.', 'error');

      },
    });
  }

  // Reuse your shift selection toggles, identical to your create component
  isShiftSelected(shift: Shift): boolean {
    return this.selectedShifts.some(
      (s) => s.day === shift.day && s.startTime === shift.startTime && s.endTime === shift.endTime
    );
  }
  toggleCellSelection(dayName: string, startTime: string) {
    const foundShift = this.allShifts.find(
      (s) => s.day === dayName && s.startTime === startTime
    );
    if (!foundShift) return;

    if (this.isShiftSelected(foundShift)) {
      // remove it
      this.selectedShifts = this.selectedShifts.filter(
        (s) =>
          !(
            s.day === foundShift.day &&
            s.startTime === foundShift.startTime &&
            s.endTime === foundShift.endTime
          )
      );
    } else {
      this.selectedShifts.push(foundShift);
    }
  }
  isRowSelected(time: string): boolean {
    return this.uniqueDays.every((day) => {
      const shift = this.allShifts.find((s) => s.day === day && s.startTime === time);
      return shift && this.isShiftSelected(shift);
    });
  }
  toggleRow(time: string) {
    const fullySelected = this.isRowSelected(time);
    if (fullySelected) {
      this.selectedShifts = this.selectedShifts.filter((sel) => sel.startTime !== time);
    } else {
      for (const day of this.uniqueDays) {
        const shift = this.allShifts.find((s) => s.day === day && s.startTime === time);
        if (shift && !this.isShiftSelected(shift)) {
          this.selectedShifts.push(shift);
        }
      }
    }
  }
  check9to5() {
    const weekdays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];
    for (const shift of this.allShifts) {
      const hour = parseInt(shift.startTime.split(':')[0], 10);
      if (weekdays.includes(shift.day) && hour >= 9 && hour < 17) {
        if (!this.isShiftSelected(shift)) {
          this.selectedShifts.push(shift);
        }
      }
    }
  }
  uncheckWeekends() {
    this.selectedShifts = this.selectedShifts.filter(
      (s) => s.day !== 'Saturday' && s.day !== 'Sunday'
    );
  }
  clearAllCheckedHours() {
    this.selectedShifts = [];
  }
  getShift(day: string, time: string): Shift | null {
    return this.allShifts.find((s) => s.day === day && s.startTime === time) || null;
  }
  isShiftSelectedByDayTime(day: string, time: string): boolean {
    const shift = this.getShift(day, time);
    return shift ? this.isShiftSelected(shift) : false;
  }
}

// Sorting functions
function sortDays(a: string, b: string) {
  const order = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
  return order.indexOf(a) - order.indexOf(b);
}
function sortTimes(a: string, b: string) {
  const aH = parseInt(a.split(':')[0], 10);
  const bH = parseInt(b.split(':')[0], 10);
  return aH - bH;
}
