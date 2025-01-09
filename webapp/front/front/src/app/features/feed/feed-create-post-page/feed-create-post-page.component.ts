import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  Tag,
  Channel,
  PostService,
  TagService,
  LinkKey,
  Roles,
} from '@shared/index';
import {
  HateoasLinksService,
  UserSessionService,
  ImageService,
  ToastService,
  AuthService,
} from '@core/index';
import { catchError, combineLatest, forkJoin, of, switchMap, take } from 'rxjs';

@Component({
  selector: 'app-create-post-page',
  templateUrl: './feed-create-post-page.component.html',
})
export class FeedCreatePostPageComponent implements OnInit {
  // Reactive form
  createPostForm: FormGroup;

  // Channel data
  channelList: Channel[] = [];
  channel: string;
  title: string = '';

  // Tag pagination data
  tagList: Tag[] = []; // Fetched tags for the current page
  appliedTags: Tag[] = []; // Tags the user has chosen

  // Pagination
  currentPage: number = 1;
  pageSize: number = 20;
  totalPages: number = 1;

  // For image preview, etc.
  imagePreviewUrl: string | ArrayBuffer;
  fileUploadError: string;

  // For channel logic
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
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const userRole = this.authService.getCurrentRole();
    this.notWorker = userRole !== Roles.WORKER;

    // Build form
    this.createPostForm = this.fb.group({
      title: ['', Validators.required],
      body: ['', Validators.required],
      imageFile: [null],
      tags: [[]], // Will store final tags array here
      channel: [''],
      user: [''],
    });

    // Fetch channels from links
    this.feedChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_FEED_CHANNEL
    );
    this.announcementsChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL
    );
    this.complaintsChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_COMPLAINTS_CHANNEL
    );

    // Listen to route queryParams for channel
    this.route.queryParams.subscribe((params) => {
      this.channel = params['inChannel'];
      this.updateChannelTitle();
    });

    // If user is not a worker, load tags
    if (this.notWorker) {
      this.fetchTags(this.currentPage);
    }
  }

  // Determine the title based on channel
  updateChannelTitle() {
    if (this.channel === this.feedChannelUrl) {
      this.title = 'Create Post';
    } else if (this.channel === this.announcementsChannelUrl) {
      this.title = 'Create Announcement';
    } else if (this.channel === this.complaintsChannelUrl) {
      this.title = 'Create Complaint';
    } else {
      this.title = 'Create Content';
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
      error: (err) => {
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
    console.log(tagName);
    if (!tagName || !tagName.trim()) return;

    const newTag: Tag = {
      name: tagName.trim(),
      self: null, // Mark as `null` since it hasn't been created yet
    };

    // Add it to applied tags
    this.addTagToApplied(newTag);
  }

  addTagToApplied(tag: Tag) {
    const exists = this.appliedTags.find((t) => t.name === tag.name);
    if (exists) {
      this.toastService.showToast(
        `You've already selected the tag '${tag.name}'`,
        'warning'
      );
      return;
    }
    this.appliedTags.push(tag);
  }

  removeTag(tag: Tag) {
    this.appliedTags = this.appliedTags.filter((t) => t.name !== tag.name);
  }

  // ---- IMAGE HANDLING ----

  onFileChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.createPostForm.patchValue({ imageFile: file });

      // Preview
      const reader = new FileReader();
      reader.onload = (e) => (this.imagePreviewUrl = reader.result);
      reader.readAsDataURL(file);
    }
  }

  // ---- FORM SUBMIT ----

  onSubmit(): void {
    if (this.createPostForm.invalid) {
      console.error('Form is invalid');
      return;
    }

    // Separate new tags from existing tags
    const newTags = this.appliedTags.filter((tag) => !tag.self);
    const existingTagUrls = this.appliedTags
      .filter((tag) => tag.self)
      .map((tag) => tag.self);

    // 1. Create new tags on the server
    const createTagsObservables = newTags.map((tag) =>
      this.tagService.createTag(tag.name).pipe(
        switchMap((location: string | null) => {
          if (!location) {
            console.error('Failed to create tag:', tag.name);
            return of(null);
          }
          return this.tagService.getTag(location); // Fetch full tag details
        }),
        catchError((err) => {
          console.error('Error creating tag:', tag.name, err);
          return of(null);
        })
      )
    );

    forkJoin(createTagsObservables).subscribe({
      next: (createdTags) => {
        const createdTagUrls = createdTags
          .filter((tag) => tag !== null)
          .map((tag: Tag) => tag.self);

        // Combine created tags and existing tags
        const allTagUrls = [...existingTagUrls, ...createdTagUrls];

        // 2. Patch tags into the form
        this.createPostForm.patchValue({
          tags: allTagUrls,
          channel: this.channel,
        });

        // 3. Build the final payload
        const formValue = { ...this.createPostForm.value };

        // Get the current user and create the post
        this.userSessionService
          .getCurrentUser()
          .pipe(
            take(1),
            switchMap((user) => {
              formValue.user = user.self;

              // Optionally handle image
              return this.createImageObservable(formValue.imageFile).pipe(
                switchMap((imageUrl) => {
                  if (imageUrl) {
                    formValue.image = imageUrl;
                  }
                  return this.postService.createPost(formValue);
                })
              );
            })
          )
          .subscribe({
            next: () => {
              const contentType = this.getContentType();
              this.toastService.showToast(
                `Your ${contentType} was created successfully!`,
                'success'
              );
              // Optionally navigate away
              this.router.navigate(['/posts'], {
                queryParams: { inChannel: this.channel },
              });
            },
            error: (error) => {
              const contentType = this.getContentType();
              this.toastService.showToast(
                `There was a problem creating your ${contentType}.`,
                'error'
              );
              console.error('Error creating post:', error);
            },
          });
      },
      error: (err) => {
        console.error('Error creating tags:', err);
      },
    });
  }

  /**
   * Returns the type of content (Post, Announcement, Complaint) based on the channel.
   */
  getContentType(): string {
    if (this.channel === this.feedChannelUrl) {
      return 'post';
    } else if (this.channel === this.announcementsChannelUrl) {
      return 'announcement';
    } else if (this.channel === this.complaintsChannelUrl) {
      return 'complaint';
    }
    return 'content'; // Fallback
  }

  /**
   * Upload image if present.
   */
  private createImageObservable(imageFile: File | null) {
    if (!imageFile) {
      return of(null);
    }
    return this.imageService.createImage(imageFile).pipe(
      catchError((err) => {
        console.error('Error uploading image:', err);
        return of(null);
      })
    );
  }
}
