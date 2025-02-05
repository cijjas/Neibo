import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import {
  Tag,
  Channel,
  PostService,
  TagService,
  LinkKey,
  Role,
} from '@shared/index';
import {
  HateoasLinksService,
  UserSessionService,
  ImageService,
  ToastService,
  AuthService,
} from '@core/index';
import { catchError, combineLatest, forkJoin, of, switchMap, take } from 'rxjs';
import { VALIDATION_CONFIG } from '@shared/constants/validation-config';

@Component({
  selector: 'app-create-post-page',
  templateUrl: './feed-create-post-page.component.html',
})
export class FeedCreatePostPageComponent implements OnInit {
  // Reactive form
  createPostForm: FormGroup;

  // Channel data
  channel: string;
  title: string = '';

  // Tag pagination data
  tagList: Tag[] = [];
  appliedTags: Tag[] = [];

  // Pagination
  currentPage: number = 1;
  pageSize: number = 20;
  totalPages: number = 1;

  // (UPDATED) For image preview
  imagePreviewUrl: string | ArrayBuffer | null = null;
  fileUploadError: string;

  // For channel logic (unchanged)
  feedChannelUrl: string;
  announcementsChannelUrl: string;
  complaintsChannelUrl: string;

  // Roles
  notWorker: boolean = true;

  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private tagService: TagService,
    private imageService: ImageService,
    private linkService: HateoasLinksService,
    private router: Router,
    private route: ActivatedRoute,
    private userSessionService: UserSessionService,
    private toastService: ToastService,
    private authService: AuthService,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    const userRole = this.userSessionService.getCurrentRole();
    this.notWorker = userRole !== Role.WORKER;

    // Build form
    this.createPostForm = this.fb.group({
      title: [
        '',
        [
          Validators.required,
          Validators.maxLength(VALIDATION_CONFIG.name.maxLength),
        ],
      ],
      body: [
        '',
        [
          Validators.required,
          Validators.maxLength(VALIDATION_CONFIG.description.maxLength),
        ],
      ],
      imageFile: [null, VALIDATION_CONFIG.imageValidator],
      tags: [[]], // Will store final tags array here
      channel: [''],
      user: [''],
    });

    // Fetch channels from links
    this.feedChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_FEED_CHANNEL,
    );
    this.announcementsChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL,
    );
    this.complaintsChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_COMPLAINTS_CHANNEL,
    );

    // Listen to route queryParams for channel
    this.route.queryParams.subscribe(params => {
      this.channel = params['inChannel'];
      this.updateChannelTitle();
    });

    // If user is not a worker, load tags
    if (this.notWorker) {
      this.fetchTags(this.currentPage);
    }
  }

  // ------------------------------------
  // (NEW) Helper to get imageFile control
  // ------------------------------------
  get imageFileControl() {
    return this.createPostForm.get('imageFile');
  }

  // ------------------------------------
  // (UPDATED) IMAGE HANDLING
  // ------------------------------------
  onFileChange(event: any) {
    const file = event.target.files?.[0];
    if (!file) return;

    // Patch the file into the form control
    this.createPostForm.patchValue({ imageFile: file });
    // Mark as touched so errors appear if invalid
    this.createPostForm.get('imageFile')?.markAsTouched();

    // If invalid, remove preview
    if (this.imageFileControl?.errors) {
      this.imagePreviewUrl = null;
      return;
    }

    // If valid, show preview
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreviewUrl = reader.result;
    };
    reader.readAsDataURL(file);
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    // Optionally add a CSS class to highlight the area
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
    this.createPostForm.patchValue({ imageFile: file });
    this.createPostForm.get('imageFile')?.markAsTouched();

    if (this.imageFileControl?.errors) {
      this.imagePreviewUrl = null;
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreviewUrl = reader.result;
    };
    reader.readAsDataURL(file);
  }

  removeImage(): void {
    this.imagePreviewUrl = null;

    const imageControl = this.createPostForm.get('imageFile');
    if (imageControl) {
      // Reset the control
      imageControl.patchValue(null);
      imageControl.setErrors(null);
      imageControl.markAsPristine();
      imageControl.markAsUntouched();
    }

    // Also clear the native <input type="file">
    const input = document.getElementById('images') as HTMLInputElement;
    if (input) {
      input.value = '';
    }
  }

  // Determine the title based on channel
  updateChannelTitle() {
    if (this.channel === this.feedChannelUrl) {
      this.title = this.translate.instant('FEED-CREATE-POST-PAGE.CREATE_POST');
    } else if (this.channel === this.announcementsChannelUrl) {
      this.title = this.translate.instant(
        'FEED-CREATE-POST-PAGE.CREATE_ANNOUNCEMENT',
      );
    } else if (this.channel === this.complaintsChannelUrl) {
      this.title = this.translate.instant(
        'FEED-CREATE-POST-PAGE.CREATE_COMPLAINT',
      );
    } else {
      this.title = this.translate.instant(
        'FEED-CREATE-POST-PAGE.CREATE_CONTENT',
      );
    }
  }

  // ---- TAGS & PAGINATION ----

  /**
   * Fetch tags from the backend for the specified page.
   */
  fetchTags(page: number) {
    const tagsUrl = this.linkService.getLink(LinkKey.NEIGHBORHOOD_TAGS);

    // Typically, your TagService might accept queryParams for pagination
    const queryParams = {
      page,
      size: this.pageSize,
    };

    this.tagService.getTags(tagsUrl, queryParams).subscribe({
      next: (response: { tags: Tag[]; totalPages: number }) => {
        this.tagList = response.tags || [];
        this.totalPages = response.totalPages || 1;
      },
      error: err => {
        console.error('Error fetching paginated tags:', err);
      },
    });
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.goToPage(this.currentPage + 1);
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.goToPage(this.currentPage - 1);
    }
  }

  goToPage(page: number): void {
    // Basic bounds check
    if (page < 1 || page > this.totalPages) return;

    this.currentPage = page;
    this.fetchTags(this.currentPage);
  }

  createCustomTag(tagName: string) {
    if (!tagName || !tagName.trim()) return;

    // Convert to CamelCase
    const formattedTag = this.convertToCamelCase(tagName);

    if (!this.isValidTag(formattedTag)) {
      this.toastService.showToast(
        (this.title = this.translate.instant(
          'FEED-CREATE-POST-PAGE.INVALID_TAG_FORMAT_USE_LETTERS_ONLY',
        )),
        'warning',
      );
      return;
    }

    if (this.appliedTags.length >= 5) {
      this.toastService.showToast(
        (this.title = this.translate.instant(
          'FEED-CREATE-POST-PAGE.YOU_CAN_ONLY_ADD_UP_TO_5_TAGS',
        )),
        'warning',
      );
      return;
    }

    const newTag: Tag = {
      name: formattedTag,
      self: null, // Mark as `null` since it hasn't been created yet
    };

    this.addTagToApplied(newTag);
  }

  addTagToApplied(tag: Tag) {
    const exists = this.appliedTags.find(t => t.name === tag.name);
    if (exists) {
      this.toastService.showToast(
        this.translate.instant(
          'FEED-CREATE-POST-PAGE.YOUVE_ALREADY_SELECTED_THE_TAG_TAGNAME',
          { tagName: tag.name },
        ),
        'warning',
      );
      return;
    }

    if (this.appliedTags.length >= 5) {
      this.toastService.showToast(
        this.translate.instant(
          'FEED-CREATE-POST-PAGE.YOU_CAN_ONLY_ADD_UP_TO_5_TAGS',
        ),
        'warning',
      );
      return;
    }

    this.appliedTags.push(tag);
  }

  private convertToCamelCase(input: string): string {
    return input
      .toLowerCase()
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join('');
  }

  private isValidTag(tag: string): boolean {
    // Allow only alphabetic characters in the tag
    return /^[a-zA-Z]+$/.test(tag);
  }

  removeTag(tag: Tag) {
    this.appliedTags = this.appliedTags.filter(t => t.name !== tag.name);
  }

  // ---- FORM SUBMIT ----

  onSubmit(): void {
    if (this.createPostForm.invalid) {
      console.error('Form is invalid');
      return;
    }

    // Separate new tags from existing tags
    const newTags = this.appliedTags.filter(tag => !tag.self);
    const existingTagUrls = this.appliedTags
      .filter(tag => tag.self)
      .map(tag => tag.self);

    // 1. Create new tags on the server only for newTags
    const createTagsObservables = newTags.map(tag =>
      this.tagService.createTag(tag.name).pipe(
        switchMap((location: string | null) => {
          if (!location) {
            console.error('Failed to create tag:', tag.name);
            return of(null);
          }
          // Fetch the newly created Tag resource to get its full details
          return this.tagService.getTag(location);
        }),
        catchError(err => {
          console.error('Error creating tag:', tag.name, err);
          return of(null);
        }),
      ),
    );

    // IMPORTANT: If createTagsObservables is empty, forkJoin([]) completes without emitting
    // so we replace it with `of([])` so the `next` block always fires.
    const tagsForkJoin$ = createTagsObservables.length
      ? forkJoin(createTagsObservables)
      : of([]);

    tagsForkJoin$.subscribe({
      next: createdTags => {
        // 'createdTags' will be an array of Tag objects (or nulls), or [] if none were created

        const createdTagUrls = createdTags
          .filter((tag): tag is Tag => !!tag)
          .map(tag => tag.self);

        // Combine any newly created Tag URLs + existing Tag URLs
        const allTagUrls = [...existingTagUrls, ...createdTagUrls];

        // 2. Patch tags into the form
        this.createPostForm.patchValue({
          tags: allTagUrls,
          channel: this.channel,
        });

        // 3. Build the final payload
        const formValue = { ...this.createPostForm.value };
        this.userSessionService
          .getCurrentUser()
          .pipe(
            take(1),
            switchMap(user => {
              formValue.user = user.self;
              // Optionally handle image
              return this.createImageObservable(formValue.imageFile).pipe(
                switchMap(imageUrl => {
                  if (imageUrl) {
                    formValue.image = imageUrl;
                  }
                  return this.postService.createPost(formValue);
                }),
              );
            }),
          )
          .subscribe({
            next: () => {
              const contentType = this.getContentType();
              this.toastService.showToast(
                this.translate.instant(
                  'FEED-CREATE-POST-PAGE.YOUR_CONTENTTYPE_WAS_CREATED_SUCCESSFULLY',
                  { contentType: contentType },
                ),
                'success',
              );
              // Navigate away if needed
              this.router.navigate(['/posts'], {
                queryParams: { inChannel: this.channel },
              });
            },
            error: error => {
              const contentType = this.getContentType();
              this.toastService.showToast(
                this.translate.instant(
                  'FEED-CREATE-POST-PAGE.THERE_WAS_A_PROBLEM_CREATING_YOUR_CONTENTTYPE',
                  { contentType: contentType },
                ),
                'error',
              );
              console.error('Error creating post:', error);
            },
          });
      },
      error: err => {
        console.error('Error creating tags in forkJoin:', err);
      },
    });
  }

  /**
   * Returns the type of content (Post, Announcement, Complaint) based on the channel.
   */
  getContentType(): string {
    if (this.channel === this.feedChannelUrl) {
      return this.translate.instant('FEED-CREATE-POST-PAGE.POST');
    } else if (this.channel === this.announcementsChannelUrl) {
      return this.translate.instant('FEED-CREATE-POST-PAGE.ANNOUNCEMENT');
    } else if (this.channel === this.complaintsChannelUrl) {
      return this.translate.instant('FEED-CREATE-POST-PAGE.COMPLAINT');
    }
    return this.translate.instant('FEED-CREATE-POST-PAGE.CONTENT'); // Fallback
  }

  /**
   * Upload image if present.
   */
  private createImageObservable(imageFile: File | null) {
    if (!imageFile) {
      return of(null);
    }
    return this.imageService.createImage(imageFile).pipe(
      catchError(err => {
        console.error('Error uploading image:', err);
        return of(null);
      }),
    );
  }

  handleTagInputKeydown(event: KeyboardEvent, inputElement: HTMLInputElement) {
    if (event.key === 'Enter') {
      event.preventDefault(); // Prevent form submission
      this.createCustomTag(inputElement.value);
      inputElement.value = ''; // Clear the input
    }
  }
}
