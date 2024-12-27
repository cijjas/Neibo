import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-admin-contact-create-page',
  templateUrl: './admin-contact-create-page.component.html',
})
export class AdminContactCreatePageComponent implements OnInit {

  contactForm!: FormGroup;

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    this.contactForm = this.fb.group({
      contactName: ['', Validators.required],
      contactAddress: ['', Validators.required],
      contactPhone: ['', Validators.required]
    });
  }

  get contactNameControl() {
    return this.contactForm.get('contactName');
  }
  get addressControl() {
    return this.contactForm.get('contactAddress');
  }
  get phoneControl() {
    return this.contactForm.get('contactPhone');
  }

  onSubmit() {
    if (this.contactForm.invalid) {
      this.contactForm.markAllAsTouched();
      return;
    }
    console.log('Creating contact:', this.contactForm.value);
    // e.g. this.contactService.createContact(this.contactForm.value)
  }
}
