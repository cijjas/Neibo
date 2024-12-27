import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-admin-amenity-create-page',
  templateUrl: './admin-amenity-create-page.component.html',
})
export class AdminAmenityCreatePageComponent implements OnInit {

  amenityForm!: FormGroup;

  // Example data for days/times
  daysPairs = [
    { key: 1, value: 'Monday' },
    { key: 2, value: 'Tuesday' },
    { key: 3, value: 'Wednesday' },
    { key: 4, value: 'Thursday' },
    { key: 5, value: 'Friday' },
    { key: 6, value: 'Saturday' },
    { key: 7, value: 'Sunday' }
  ];

  timesPairs = [
    { key: 9, value: { key: '09:00' } },
    { key: 10, value: { key: '10:00' } },
    { key: 11, value: { key: '11:00' } },
    // ...
    { key: 17, value: { key: '17:00' } },
  ];

  selectedShifts: { day: number; time: number }[] = [];

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    this.amenityForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required]
    });
  }

  get nameControl() {
    return this.amenityForm.get('name');
  }

  get descControl() {
    return this.amenityForm.get('description');
  }

  // If at least one shift is selected, enable "Create" button
  get anyShiftChecked(): boolean {
    return this.selectedShifts.length > 0;
  }

  onSubmit() {
    if (this.amenityForm.invalid || !this.anyShiftChecked) {
      this.amenityForm.markAllAsTouched();
      return;
    }

    console.log('Creating amenity with:', this.amenityForm.value, 'Shifts:', this.selectedShifts);
    // e.g. this.amenityService.createAmenity({...})
    //   .subscribe(...)
  }

  // Utility to see if a shift (day/time) is selected
  isShiftSelected(dayKey: number, timeKey: number): boolean {
    return this.selectedShifts.some(s => s.day === dayKey && s.time === timeKey);
  }

  onShiftChange(dayKey: number, timeKey: number, checked: boolean) {
    if (checked) {
      this.selectedShifts.push({ day: dayKey, time: timeKey });
    } else {
      this.selectedShifts = this.selectedShifts.filter(s => !(s.day === dayKey && s.time === timeKey));
    }
  }

  toggleRow(rowIndex: number) {
    // Toggle all shifts for the given row
    const timeKey = this.timesPairs[rowIndex].key;
    const isAllSelected = this.daysPairs.every(d => this.isShiftSelected(d.key, timeKey));

    // If all selected, unselect them, else select them
    if (isAllSelected) {
      this.selectedShifts = this.selectedShifts.filter(s => s.time !== timeKey);
    } else {
      this.daysPairs.forEach(d => {
        if (!this.isShiftSelected(d.key, timeKey)) {
          this.selectedShifts.push({ day: d.key, time: timeKey });
        }
      });
    }
  }

  // Check 9-17 for weekdays
  check9to5() {
    const weekdays = [1, 2, 3, 4, 5];
    this.timesPairs.forEach(tp => {
      if (tp.key >= 9 && tp.key <= 17) {
        weekdays.forEach(day => {
          if (!this.isShiftSelected(day, tp.key)) {
            this.selectedShifts.push({ day, time: tp.key });
          }
        });
      }
    });
  }

  // Uncheck Saturdays & Sundays
  uncheckWeekends() {
    const weekends = [6, 7];
    this.selectedShifts = this.selectedShifts.filter(
      s => !weekends.includes(s.day)
    );
  }

  // Clear everything
  clearAllCheckedHours() {
    this.selectedShifts = [];
  }
}
