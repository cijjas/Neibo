import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import {
  ImageService,
  HateoasLinksService,
  UserSessionService,
  ToastService,
} from '@core/index';
import { PostService, TagService, LinkKey } from '@shared/index';
import { catchError, forkJoin, of, switchMap, take } from 'rxjs';

/** Minimal Tag interface. If you already have a Tag type, you can remove this. */
interface Tag {
  name: string;
  self: string | null;
}

export function atLeastOneTagSelected(
  control: AbstractControl
): ValidationErrors | null {
  const tags = control.value || [];
  return tags.length > 0 ? null : { noTagsSelected: true };
}

@Component({
  selector: 'app-admin-create-announcement-page',
  templateUrl: './admin-create-announcement-page.component.html',
})
export class AdminCreateAnnouncementPageComponent implements OnInit {
  /** Main announcement form. */
  announcementForm!: FormGroup;

  /** Image preview. */
  imagePreviewUrl: string | ArrayBuffer | null = null;
  fileUploadError: string | null = null;

  /**
   * Hardcoded default tags. If you don’t know whether these exist in
   * the backend, we set `self: null` by default. On submit,
   * we create them if needed.
   */
  defaultTags: Tag[] = [
    { name: 'Event', self: null },
    { name: 'Notice', self: null },
    { name: 'Important', self: null },
    { name: 'Urgent', self: null },
    { name: 'Reminder', self: null },
    { name: 'Information', self: null },
    { name: 'Alert', self: null },
    { name: 'Security', self: null },
    { name: 'Maintenance', self: null },
    { name: 'Weather', self: null },
    { name: 'Outage', self: null },
    { name: 'Safety', self: null },
    { name: 'Meeting', self: null },
    { name: 'Gathering', self: null },
    { name: 'Regulation', self: null },
    { name: 'Guideline', self: null },
    { name: 'Restriction', self: null },
    { name: 'Policy', self: null },
    { name: 'Repair', self: null },
    { name: 'Landscaping', self: null },
    { name: 'Utilities', self: null },
    { name: 'Deadline', self: null },
    { name: 'Schedule', self: null },
    { name: 'Temporary', self: null },
  ];

  /** Tags that the user has actually selected. */
  appliedTags: Tag[] = [];

  /** Link to the announcements channel. */
  channel: string;

  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private tagService: TagService,
    private imageService: ImageService,
    private linkService: HateoasLinksService,
    private userSessionService: UserSessionService,
    private toastService: ToastService
  ) {
    // Get the link to your announcements channel
    this.channel = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL
    );
  }

  ngOnInit(): void {
    this.announcementForm = this.fb.group({
      subject: ['', Validators.required],
      message: ['', Validators.required],
      imageFile: [null],
      tags: [[], atLeastOneTagSelected],
    });
  }

  // --- Accessors for easy error checks ---
  get subjectControl() {
    return this.announcementForm.get('subject');
  }
  get messageControl() {
    return this.announcementForm.get('message');
  }

  // --------------------------------------------------
  // IMAGE HANDLING
  // --------------------------------------------------
  onFileChange(event: any) {
    const file = event.target.files[0];
    if (!file) {
      this.imagePreviewUrl = null;
      this.announcementForm.patchValue({ imageFile: null });
      return;
    }

    // Update the form
    this.announcementForm.patchValue({ imageFile: file });

    // Create a preview
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreviewUrl = reader.result;
    };
    reader.readAsDataURL(file);
  }

  // --------------------------------------------------
  // TAGS: Only from default set
  // --------------------------------------------------
  addTagToApplied(tag: Tag) {
    const exists = this.appliedTags.some((t) => t.name === tag.name);
    if (exists) {
      this.toastService.showToast(
        `You've already selected the tag '${tag.name}'`,
        'warning'
      );
      return;
    }
    this.appliedTags.push(tag);

    // Update the tags control value
    this.announcementForm
      .get('tags')
      ?.setValue(this.appliedTags.map((t) => t.self));
  }

  removeTag(tag: Tag) {
    this.appliedTags = this.appliedTags.filter((t) => t.name !== tag.name);

    // Update the tags control value
    this.announcementForm
      .get('tags')
      ?.setValue(this.appliedTags.map((t) => t.self));
  }

  // --------------------------------------------------
  // SUBMIT
  // --------------------------------------------------
  onSubmit() {
    if (this.announcementForm.invalid) {
      this.announcementForm.markAllAsTouched();
      return;
    }

    // Separate new (no self link) from existing (already has self)
    const newTags = this.appliedTags.filter((t) => t.self === null);
    const existingTags = this.appliedTags.filter((t) => t.self !== null);

    // Extract the existing tag `self` links
    const existingTagUrls = existingTags.map((t) => t.self as string);

    // 1. For new tags, call createTag(...)
    const createTagsObservables = newTags.map((tag) =>
      this.tagService.createTag(tag.name).pipe(
        switchMap((location: string | null) => {
          if (!location) {
            console.error('Failed to create tag:', tag.name);
            return of(null);
          }
          // After creation, get the full Tag to retrieve the `self` link
          return this.tagService.getTag(location);
        }),
        catchError((err) => {
          console.error('Error creating tag:', tag.name, err);
          return of(null);
        })
      )
    );

    // 2. Wait for all new tags to be created
    const newTags$ = createTagsObservables.length
      ? forkJoin(createTagsObservables)
      : of([]); // <-- if no new tags, just emit [] immediately

    newTags$.subscribe({
      next: (createdTags) => {
        // Now this always fires

        // Extract `self` from newly created tags
        const createdTagUrls = createdTags
          .filter((t) => t !== null)
          .map((t: any) => t.self);

        // Combine existing + newly created
        const allTagUrls = [...existingTagUrls, ...createdTagUrls];

        // Patch them into the form
        this.announcementForm.patchValue({ tags: allTagUrls });

        // Final step: create the announcement post
        this.createAnnouncement();
      },
      error: (err) => {
        console.error('Error creating tags:', err);
        this.toastService.showToast(
          'Some tags could not be created. Please try again.',
          'error'
        );
      },
    });
  }

  /**
   * Actually calls PostService to create the announcement.
   */
  private createAnnouncement() {
    const formValue = { ...this.announcementForm.value };

    this.userSessionService
      .getCurrentUser()
      .pipe(
        take(1),
        switchMap((user) => {
          // Map fields to match backend expectations
          const payload = {
            title: formValue.subject, // Map "subject" to "title"
            body: formValue.message, // Map "message" to "body"
            tags: formValue.tags.filter((url) => url !== null), // Ensure valid tag URLs
            user: user.self, // User link
            channel: this.channel, // Channel link
            image: null, // Default to null unless an image is uploaded
          };

          // Handle image upload if present
          return this.createImageObservable(formValue.imageFile).pipe(
            switchMap((imageUrl) => {
              if (imageUrl) {
                payload.image = imageUrl;
              }
              return this.postService.createPost(payload);
            })
          );
        })
      )
      .subscribe({
        next: () => {
          this.toastService.showToast(
            'Announcement created successfully!',
            'success'
          );
          this.resetForm();
        },
        error: (err) => {
          console.error('Error creating announcement:', err);
          this.toastService.showToast(
            'Error creating announcement. Please try again.',
            'error'
          );
        },
      });
  }

  private resetForm() {
    this.announcementForm.reset();
    this.announcementForm.markAsPristine();
    this.appliedTags = [];
    this.imagePreviewUrl = null;
    this.fileUploadError = null;
  }

  /**
   * Helper that uploads the image if present, returning an observable of the image URL or null.
   */
  private createImageObservable(imageFile: File | null) {
    if (!imageFile) {
      return of(null);
    }
    return this.imageService.createImage(imageFile).pipe(
      catchError((err) => {
        console.error('Error uploading image:', err);
        this.fileUploadError = 'Error uploading image';
        return of(null);
      })
    );
  }
}
