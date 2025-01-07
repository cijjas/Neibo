import {
  Component,
  OnInit,
  AfterViewInit,
  ElementRef,
  ViewChild,
} from '@angular/core';
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
} from '@core/index';
import { catchError, combineLatest, forkJoin, of, switchMap, take } from 'rxjs';

@Component({
  selector: 'app-create-post-page',
  templateUrl: './feed-create-post-page.component.html',
})
export class FeedCreatePostPageComponent implements OnInit, AfterViewInit {
  @ViewChild('tagInput1', { static: true }) tagInput1Ref!: ElementRef;

  // Reactive form
  createPostForm: FormGroup;

  // Channel data (replace with real)
  channelList: Channel[] = [];
  // Tag suggestions
  tagList: Tag[] = [];

  // The plugin instance
  private tagInput1: any;

  // For image preview, etc.
  imagePreviewUrl: string | ArrayBuffer;
  fileUploadError: string;
  showSuccessMessage = false;

  // For channel logic
  feedChannelUrl: string;
  announcementsChannelUrl: string;
  complaintsChannelUrl: string;
  channel: string;
  title: string = '';

  // Placeholder text used by the plugin
  placeholderText: string = 'Enter a tag';

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
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    const userRole = this.userSessionService.getCurrentRole();
    this.notWorker = userRole !== Roles.WORKER;

    // Build form
    this.createPostForm = this.fb.group({
      title: ['', Validators.required],
      body: ['', Validators.required],
      imageFile: [null],
      tags: [[]], // We'll store final tags array here
      channel: [''],
      user: [''],
    });

    // Fetch channels & tags
    this.feedChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_FEED_CHANNEL
    );
    this.announcementsChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL
    );
    this.complaintsChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_COMPLAINTS_CHANNEL
    );

    this.route.queryParams.subscribe((params) => {
      this.channel = params['inChannel'];
      this.updateChannelTitle();
    });

    if (this.notWorker) this.fetchTags();
  }

  ngAfterViewInit(): void {
    if (this.notWorker) {
      this.tagInput1 = new (window as any).TagsInput({
        selector: 'tag-input1',
        wrapperClass: 'tags-input-wrapper',
        tagClass: 'tag',
        duplicate: false,
        max: 5,
      });
    }
  }

  addTagToApply(tagName: string): void {
    if (this.tagInput1) {
      this.tagInput1.addTag(tagName);
    }
  }

  updateChannelTitle() {
    if (this.channel === this.feedChannelUrl) {
      this.title = 'Create Post';
    } else if (this.channel === this.announcementsChannelUrl) {
      this.title = 'Create Announcement';
    } else if (this.channel === this.complaintsChannelUrl) {
      this.title = 'Create Complaint';
    }
  }

  fetchTags(): void {
    const tagsUrl = this.linkService.getLink(LinkKey.NEIGHBORHOOD_TAGS);
    this.tagService.getTags(tagsUrl).subscribe((tags: any) => {
      this.tagList = tags; // e.g. [{ name: 'JavaScript', self: '...' }, ...]
    });
  }

  onFileChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.createPostForm.patchValue({ imageFile: file });

      // File Preview
      const reader = new FileReader();
      reader.onload = (e) => (this.imagePreviewUrl = reader.result);
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    if (this.createPostForm.invalid) {
      console.error('Form is invalid');
      return;
    }

    // 1) Get the final tags from the plugin
    const selectedTags = this.tagInput1?.arr || [];

    // 2) Patch them into the form so the backend can store them
    this.createPostForm.patchValue({
      tags: selectedTags,
      channel: this.channel,
    });

    // Build final payload
    const formValue = { ...this.createPostForm.value };

    this.userSessionService
      .getCurrentUser()
      .pipe(
        take(1),
        switchMap((user) => {
          formValue.user = user.self;

          // Create tags + image, then post
          return combineLatest([
            this.createTagsObservable(selectedTags),
            this.createImageObservable(formValue.imageFile),
          ]);
        }),
        switchMap(([tagUrls, imageUrl]) => {
          formValue.tags = tagUrls.filter((tag) => tag !== null);
          if (imageUrl) formValue.image = imageUrl;

          return this.postService.createPost(formValue);
        })
      )
      .subscribe({
        next: () => {
          const contentType = this.getContentType(); // Get the content type dynamically
          this.toastService.showToast(
            `Your ${contentType} was created successfully!`,
            'success'
          );
          this.router.navigate(['/posts'], {
            queryParams: { inChannel: this.channel },
          });
        },
        error: (error) => {
          const contentType = this.getContentType(); // Get the content type dynamically
          this.toastService.showToast(
            `There was a problem creating your ${contentType}.`,
            'error'
          );
          console.error('Error creating post:', error);
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
    return 'content'; // Fallback if channel doesn't match known types
  }

  /**
   * Creates tags on the backend. If you create them individually,
   * you can return their URLs or IDs to attach to the post.
   */
  private createTagsObservable(tags: string[]) {
    if (!tags || tags.length === 0) {
      return of([]);
    }
    // Create each tag in parallel
    return forkJoin(
      tags.map((tag) =>
        this.tagService.createTag(tag).pipe(
          catchError((err) => {
            console.error(`Error creating tag: ${tag}`, err);
            return of(null);
          })
        )
      )
    );
  }

  /**
   * Upload image if present
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
