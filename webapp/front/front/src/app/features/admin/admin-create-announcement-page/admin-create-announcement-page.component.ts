import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-admin-create-announcement-page',
  templateUrl: './admin-create-announcement-page.component.html',
})
export class AdminCreateAnnouncementPageComponent implements OnInit {

  announcementForm!: FormGroup;
  previewImage: string | ArrayBuffer | null = null;

  // Example tags
  availableTags: string[] = [
    'Event',
    'Notice',
    'Important',
    'Urgent',
    // etc...
  ];

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    // Build form with Angular
    this.announcementForm = this.fb.group({
      subject: ['', Validators.required],
      message: ['', Validators.required],
      // We will handle file in a separate variable or in the service
    });
  }

  get subjectControl() {
    return this.announcementForm.get('subject');
  }

  get messageControl() {
    return this.announcementForm.get('message');
  }

  // Called on form submit
  onSubmit() {
    if (this.announcementForm.invalid) {
      this.announcementForm.markAllAsTouched();
      return;
    }

    // Normally you'd call a service method to POST the announcement
    console.log('Form values:', this.announcementForm.value);

    // e.g., this.announcementsService.publishAnnouncement(this.announcementForm.value)
    //       .subscribe(...)
  }

  // Preview the selected image
  preview(event: any) {
    const file = event.target.files[0];
    if (!file) {
      this.previewImage = null;
      return;
    }
    const reader = new FileReader();
    reader.onload = (e) => {
      this.previewImage = e.target?.result;
    };
    reader.readAsDataURL(file);
  }

  // Tag input logic example
  addTagToApply(tag: string) {
    if (!tag) return;
    console.log('Adding tag:', tag);
    // In a real app, you might store these in an array formControl
  }
}
