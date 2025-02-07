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
import { PostService, TagService, LinkKey, Tag } from '@shared/index';
import { catchError, forkJoin, of, switchMap, take } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { VALIDATION_CONFIG } from '@shared/constants/validation-config';
import { Title } from '@angular/platform-browser';
import { AppTitleKeys } from '@shared/constants/app-titles';

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
    private toastService: ToastService,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    const title = this.translate.instant(
      AppTitleKeys.ADMIN_CREATE_ANNOUNCEMENT_PAGE,
    );
    this.titleService.setTitle(title);

    // Get the link to your announcements channel
    this.channel = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL,
    );

    this.announcementForm = this.fb.group({
      subject: ['', Validators.required],
      message: ['', Validators.required],
      imageFile: [null, VALIDATION_CONFIG.imageValidator],
      tags: [[], VALIDATION_CONFIG.atLeastOneTagSelected],
    });
  }

  // --- Accessors for easy error checks ---
  get subjectControl() {
    return this.announcementForm.get('subject');
  }
  get messageControl() {
    return this.announcementForm.get('message');
  }
  get imageFileControl() {
    return this.announcementForm.get('imageFile');
  }

  // --------------------------------------------------
  // IMAGE HANDLING
  // --------------------------------------------------
  onFileChange(event: any) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];
    const imageControl = this.announcementForm.get('imageFile');

    if (!imageControl) return;

    // Patch the file
    imageControl.patchValue(file);
    imageControl.markAsTouched();
    imageControl.updateValueAndValidity();

    // If invalid, do NOT remove file from the control
    if (imageControl.errors) {
      // Hide preview
      this.imagePreviewUrl = null;
      // The user sees the error in <app-form-error>.
      // They also still see the file's name in <input type="file"> by default.
      return;
    }

    // If valid, show the preview
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreviewUrl = reader.result;
    };
    reader.readAsDataURL(file);
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    // Might set a CSS class to highlight the drop area
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      const file = event.dataTransfer.files[0];
      this.handleDroppedFile(file);
    }
  }

  handleDroppedFile(file: File) {
    // For instance, patch the form or feed it to your onFileChange logic
    this.announcementForm.patchValue({ imageFile: file });
    this.announcementForm.get('imageFile')?.updateValueAndValidity();

    // If you want a preview, do:
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreviewUrl = reader.result;
    };
    reader.readAsDataURL(file);
  }

  removeImage(): void {
    this.imagePreviewUrl = null;

    const imageControl = this.announcementForm.get('imageFile');
    if (imageControl) {
      // Reset the control
      imageControl.patchValue(null);
      imageControl.setErrors(null);
      imageControl.markAsPristine();
      imageControl.markAsUntouched();
    }

    // Reset the native <input type="file">
    const input = document.getElementById('images') as HTMLInputElement;
    if (input) {
      input.value = '';
    }
  }

  // --------------------------------------------------
  // TAGS: Only from default set
  // --------------------------------------------------
  addTagToApplied(tag: Tag) {
    const exists = this.appliedTags.some(t => t.name === tag.name);
    if (exists) {
      this.toastService.showToast(
        this.translate.instant(
          'ADMIN-CREATE-ANNOUNCEMENT-PAGE.YOUVE_ALREADY_SELECTED_THE_TAG_TAGNAME',
          {
            tagName: tag.name,
          },
        ),
        'warning',
      );
      return;
    }
    this.appliedTags.push(tag);

    // Update the tags control value
    this.announcementForm
      .get('tags')
      ?.setValue(this.appliedTags.map(t => t.self));
  }

  removeTag(tag: Tag) {
    this.appliedTags = this.appliedTags.filter(t => t.name !== tag.name);

    // Update the tags control value
    this.announcementForm
      .get('tags')
      ?.setValue(this.appliedTags.map(t => t.self));
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
    const newTags = this.appliedTags.filter(t => t.self === null);
    const existingTags = this.appliedTags.filter(t => t.self !== null);

    // Extract the existing tag `self` links
    const existingTagUrls = existingTags.map(t => t.self as string);

    // 1. For new tags, call createTag(...)
    const createTagsObservables = newTags.map(tag =>
      this.tagService.createTag(tag.name).pipe(
        switchMap((location: string | null) => {
          if (!location) {
            console.error(
              this.translate.instant(
                'ADMIN-CREATE-ANNOUNCEMENT-PAGE.FAILED_TO_CREATE_TAG',
              ),
              tag.name,
            );
            return of(null);
          }
          // After creation, get the full Tag to retrieve the `self` link
          return this.tagService.getTag(location);
        }),
        catchError(err => {
          console.error(
            this.translate.instant(
              'ADMIN-CREATE-ANNOUNCEMENT-PAGE.ERROR_CREATING_TAG',
            ),
            tag.name,
            err,
          );
          return of(null);
        }),
      ),
    );

    // 2. Wait for all new tags to be created
    const newTags$ = createTagsObservables.length
      ? forkJoin(createTagsObservables)
      : of([]); // <-- if no new tags, just emit [] immediately

    newTags$.subscribe({
      next: createdTags => {
        // Now this always fires

        // Extract `self` from newly created tags
        const createdTagUrls = createdTags
          .filter(t => t !== null)
          .map((t: any) => t.self);

        // Combine existing + newly created
        const allTagUrls = [...existingTagUrls, ...createdTagUrls];

        // Patch them into the form
        this.announcementForm.patchValue({ tags: allTagUrls });

        // Final step: create the announcement post
        this.createAnnouncement();
      },
      error: err => {
        console.error(
          this.translate.instant(
            'ADMIN-CREATE-ANNOUNCEMENT-PAGE.ERROR_CREATING_TAGS',
          ),
          err,
        );
        this.toastService.showToast(
          this.translate.instant(
            'ADMIN-CREATE-ANNOUNCEMENT-PAGE.SOME_TAGS_COULD_NOT_BE_CREATED_PLEASE_TRY_AGAIN',
          ),
          'error',
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
        switchMap(user => {
          // Map fields to match backend expectations
          const payload = {
            title: formValue.subject, // Map "subject" to "title"
            body: formValue.message, // Map "message" to "body"
            tags: formValue.tags.filter(url => url !== null), // Ensure valid tag URLs
            user: user.self, // User link
            channel: this.channel, // Channel link
            image: null, // Default to null unless an image is uploaded
          };

          // Handle image upload if present
          return this.createImageObservable(formValue.imageFile).pipe(
            switchMap(imageUrl => {
              if (imageUrl) {
                payload.image = imageUrl;
              }
              return this.postService.createPost(payload);
            }),
          );
        }),
      )
      .subscribe({
        next: () => {
          this.toastService.showToast(
            this.translate.instant(
              'ADMIN-CREATE-ANNOUNCEMENT-PAGE.ANNOUNCEMENT_CREATED_SUCCESSFULLY',
            ),
            'success',
          );
          this.resetForm();
        },
        error: err => {
          console.error(
            this.translate.instant(
              'ADMIN-CREATE-ANNOUNCEMENT-PAGE.ERROR_CREATING_ANNOUNCEMENT',
            ),
            err,
          );
          this.toastService.showToast(
            this.translate.instant(
              'ADMIN-CREATE-ANNOUNCEMENT-PAGE.ERROR_CREATING_ANNOUNCEMENT_PLEASE_TRY_AGAIN',
            ),
            'error',
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
      catchError(err => {
        console.error(
          this.translate.instant(
            'ADMIN-CREATE-ANNOUNCEMENT-PAGE.ERROR_UPLOADING_IMAGE',
          ),
          err,
        );
        this.fileUploadError = this.translate.instant(
          'ADMIN-CREATE-ANNOUNCEMENT-PAGE.ERROR_UPLOADING_IMAGE_2',
        );
        return of(null);
      }),
    );
  }
}
