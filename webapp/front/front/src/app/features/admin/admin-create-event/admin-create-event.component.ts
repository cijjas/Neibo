import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-admin-create-event',
  templateUrl: './admin-create-event.component.html',
})
export class AdminCreateEventComponent {
  eventForm: FormGroup;
  showSuccessMessage: boolean = false;

  constructor(private fb: FormBuilder) {
    // Initialize the form group
    this.eventForm = this.fb.group({
      name: ['', [Validators.required]],
      description: ['', [Validators.required]],
      date: ['', [Validators.required]],
      startTime: ['', [Validators.required]],
      endTime: ['', [Validators.required]],
    });
  }

  onSubmit() {
    if (this.eventForm.valid) {
      console.log('Event Data:', this.eventForm.value);

      // Simulate success
      this.showSuccessMessage = true;

      // Reset the form after a delay (simulate backend success)
      setTimeout(() => {
        this.eventForm.reset();
        this.showSuccessMessage = false;
      }, 3000);
    }
  }
}
