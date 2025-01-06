import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastService } from '@core/index';
import { ShiftService, Shift, AmenityService } from '@shared/index';

@Component({
  selector: 'app-admin-amenity-create-page',
  templateUrl: './admin-amenity-create-page.component.html',
})
export class AdminAmenityCreatePageComponent implements OnInit {
  amenityForm!: FormGroup;

  // All shifts loaded from API
  allShifts: Shift[] = [];

  // Distinct day names found in allShifts
  uniqueDays: string[] = [];

  // Distinct times found in allShifts (strings like "00:00:00", "01:00:00", etc.)
  uniqueTimes: string[] = [];

  // The user’s current selections
  selectedShifts: Shift[] = [];

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
    private shiftService: ShiftService,
    private amenityService: AmenityService,
    private toastService: ToastService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.amenityForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
    });

    // Example: adjust params as needed or remove them altogether
    this.shiftService.getShifts().subscribe({
      next: (shiftsFromApi) => {
        this.allShifts = shiftsFromApi;

        // Build a unique sorted list of days
        const daysSet = new Set<string>();
        // Build a unique sorted list of startTimes
        const timesSet = new Set<string>();

        for (const shift of this.allShifts) {
          daysSet.add(shift.day);
          timesSet.add(shift.startTime);
        }

        // Convert sets to arrays and sort them
        this.uniqueDays = Array.from(daysSet).sort(sortDays);
        this.uniqueTimes = Array.from(timesSet).sort(sortTimes);
      },
      error: (err) => console.error(err),
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
    const formValue = { ...this.amenityForm.value };

    // Assuming selectedShifts is an array of Shift objects
    const selectedShiftUrls: string[] = this.selectedShifts.map(
      (shift) => shift.self
    );

    this.amenityService
      .createAmenity(formValue.name, formValue.description, selectedShiftUrls)
      .subscribe({
        next: (next) => {
          this.toastService.showToast(
            `Amenity '${formValue.name}' created successfully!`,
            'success'
          );
          this.router.navigate(['admin/amenities']);
        },
        error: (err) => {
          this.toastService.showToast(
            `Error creating amenity '${formValue.name}' , try again later.`,
            'error'
          );
        },
      });
  }

  /**
   * Returns true if the given Shift is in the selectedShifts array
   */
  isShiftSelected(shift: Shift): boolean {
    return this.selectedShifts.some(
      (s) =>
        s.day === shift.day &&
        s.startTime === shift.startTime &&
        s.endTime === shift.endTime
    );
  }

  /**
   * For the given (day, time), locate the matching Shift object from allShifts.
   * If we find it, toggle its selection state (add to or remove from selectedShifts).
   */
  toggleCellSelection(dayName: string, startTime: string) {
    const foundShift = this.allShifts.find(
      (s) => s.day === dayName && s.startTime === startTime
    );
    if (!foundShift) {
      // If there's literally no shift for that day/time, do nothing
      return;
    }

    // If selected => remove it
    if (this.isShiftSelected(foundShift)) {
      this.selectedShifts = this.selectedShifts.filter(
        (s) =>
          !(
            s.day === foundShift.day &&
            s.startTime === foundShift.startTime &&
            s.endTime === foundShift.endTime
          )
      );
    } else {
      // Otherwise add it
      this.selectedShifts.push(foundShift);
    }
  }

  /**
   * Helper to see if an entire row (time row) is selected
   * i.e. if EVERY day in that row is selected for that time
   */
  isRowSelected(time: string): boolean {
    // For each day, check if there's a shift. If it exists, is it selected?
    // The row is "fully selected" only if for every day, the shift is selected
    return this.uniqueDays.every((day) => {
      const shift = this.allShifts.find(
        (s) => s.day === day && s.startTime === time
      );
      return shift && this.isShiftSelected(shift);
    });
  }

  /**
   * Toggle all day-cells in a given row (time)
   */
  toggleRow(time: string) {
    const fullySelected = this.isRowSelected(time);

    if (fullySelected) {
      // Unselect all in this row
      this.selectedShifts = this.selectedShifts.filter(
        (sel) => sel.startTime !== time
      );
    } else {
      // Select all shifts in this row
      for (const day of this.uniqueDays) {
        const shift = this.allShifts.find(
          (s) => s.day === day && s.startTime === time
        );
        if (shift && !this.isShiftSelected(shift)) {
          this.selectedShifts.push(shift);
        }
      }
    }
  }

  /**
   * Mark all Monday-Friday, 9-17 as selected
   */
  check9to5() {
    const weekdays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];

    for (const shift of this.allShifts) {
      const hour = parseInt(shift.startTime.split(':')[0], 10); // e.g. "09" => 9
      if (weekdays.includes(shift.day) && hour >= 9 && hour < 17) {
        // If not already selected, add it
        if (!this.isShiftSelected(shift)) {
          this.selectedShifts.push(shift);
        }
      }
    }
  }

  /**
   * Uncheck (remove) all Saturday / Sunday shifts
   */
  uncheckWeekends() {
    this.selectedShifts = this.selectedShifts.filter(
      (s) => s.day !== 'Saturday' && s.day !== 'Sunday'
    );
  }

  /**
   * Clear every selected hour
   */
  clearAllCheckedHours() {
    this.selectedShifts = [];
  }

  /**
   * Returns the Shift object for a given day and time, or null if not found
   */
  getShift(day: string, time: string): Shift | null {
    return (
      this.allShifts.find((s) => s.day === day && s.startTime === time) || null
    );
  }

  /**
   * Wrapper for isShiftSelected to avoid complex logic in the template
   */
  isShiftSelectedByDayTime(day: string, time: string): boolean {
    const shift = this.getShift(day, time);
    return shift ? this.isShiftSelected(shift) : false;
  }

  formatTime(time: string): string {
    // Assuming `time` is in the format "HH:mm:ss"
    const [hours, minutes] = time.split(':');
    return `${hours}:${minutes}`; // Return only hours and minutes
  }
}

/**
 * Example sort for days – so they appear Monday, Tuesday, etc.
 * If you want a different day ordering, adjust this function
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
  // "08:00:00" -> [8,0,0]
  const aH = parseInt(a.split(':')[0], 10);
  const bH = parseInt(b.split(':')[0], 10);
  return aH - bH;
}
