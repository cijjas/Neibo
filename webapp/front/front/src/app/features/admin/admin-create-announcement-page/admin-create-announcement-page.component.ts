import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ImageService, HateoasLinksService, UserSessionService, ToastService } from '@core/index'
import { PostService, TagService, LinkKey } from '@shared/index';
import { catchError, combineLatest, forkJoin, of, switchMap, take } from 'rxjs';

@Component({
  selector: 'app-admin-create-announcement-page',
  templateUrl: './admin-create-announcement-page.component.html',
})
export class AdminCreateAnnouncementPageComponent implements OnInit {

  announcementForm!: FormGroup;
  previewImage: string | ArrayBuffer | null = null;
  fileUploadError: string | null = null;

  // Example tags
  availableTags: string[] = [
    'Event',
    'Notice',
    'Important',
    'Urgent',
  ];
  // The tags that the user has selected (optional)
  selectedTags: string[] = [];

  channel: string = this.linkService.getLink(LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL);

  // For demonstration purposes
  loggedUser = { darkMode: false, role: 'ADMIN' };

  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private tagService: TagService,
    private imageService: ImageService,
    private linkService: HateoasLinksService,
    private userSessionService: UserSessionService,
    private toastService: ToastService
  ) { }

  ngOnInit(): void {
    // Build form with Angular
    // Named them subject & message for the UI, but we will convert to title & body
    this.announcementForm = this.fb.group({
      subject: ['', Validators.required],
      message: ['', Validators.required],
      imageFile: [null], // We’ll store the file here after preview
      tags: [[]]
    });
  }

  get subjectControl() {
    return this.announcementForm.get('subject');
  }
  get messageControl() {
    return this.announcementForm.get('message');
  }

  // ----------------------- IMAGE PREVIEW -----------------------
  preview(event: any) {
    const file = event.target.files[0];
    if (!file) {
      this.previewImage = null;
      this.announcementForm.patchValue({ imageFile: null });
      return;
    }
    // Update the form with the selected file
    this.announcementForm.patchValue({ imageFile: file });

    // Create a preview
    const reader = new FileReader();
    reader.onload = (e) => {
      this.previewImage = e.target?.result;
    };
    reader.readAsDataURL(file);
  }

  // ----------------------- TAGS -----------------------
  addTagToApply(tag: string) {
    if (!tag) return;
    // In a real app, you might store these in an array formControl
    if (!this.selectedTags.includes(tag)) {
      this.selectedTags.push(tag);
    }
  }

  removeTag(tag: string) {
    this.selectedTags = this.selectedTags.filter(t => t !== tag);
  }

  // ----------------------- SUBMIT -----------------------
  onSubmit() {
    if (this.announcementForm.invalid) {
      this.announcementForm.markAllAsTouched();
      return;
    }

    // Step 1: Get the raw values from the form
    const formValue = { ...this.announcementForm.value };

    // Step 2: Convert them to match what PostService expects
    // The PostDto typically expects 'title' and 'body'
    const newPostDto: any = {
      title: formValue.subject,
      body: formValue.message,
      channel: this.channel,     // or this.announcementsChannelUrl if using HATEOAS link
      tags: [],                  // will be filled after we create them via TagService
      imageFile: formValue.imageFile,
      user: ''                   // will be filled with user.self after we fetch the user
    };

    // Step 3: Create post logic - we replicate the pattern from FeedCreatePostPage
    this.userSessionService.getCurrentUser()
      .pipe(
        take(1),
        switchMap(user => {
          // set the user link from the user data
          newPostDto.user = user.self;

          // Create tags in backend if needed, then upload image
          return combineLatest([
            this.createTagsObservable(),
            this.createImageObservable(newPostDto.imageFile)
          ]);
        }),
        switchMap(([tagUrls, imageUrl]) => {
          // tagUrls is an array of the newly created (or existing) tag links
          newPostDto.tags = tagUrls.filter(tag => tag !== null);
          if (imageUrl) {
            newPostDto.image = imageUrl;
          }
          // Finally, POST the new announcement
          return this.postService.createPost(newPostDto);
        })
      )
      .subscribe({
        next: (location) => {
          this.toastService.showToast('Announcement created successully!', 'success');
        },
        error: (err) => {
          this.toastService.showToast('The announcement could not be made, try again.', 'error');
        },
      });
  }

  // ----------------------- SUPPORTING METHODS -----------------------
  private createTagsObservable() {
    // If you want to create the user-entered tags in the backend,
    // replicate the logic from FeedCreatePostPage. Example:
    if (this.selectedTags.length === 0) {
      return of([]);
    }
    return forkJoin(
      this.selectedTags.map(tag =>
        this.tagService.createTag(tag).pipe(
          catchError(error => {
            console.error(`Error creating tag: ${tag}`, error);
            return of(null); // continue if one fails
          })
        )
      )
    );
  }

  private createImageObservable(imageFile: File | null) {
    return imageFile
      ? this.imageService.createImage(imageFile).pipe(
        catchError(error => {
          console.error('Error uploading image:', error);
          this.fileUploadError = 'Error uploading image';
          return of(null); // continue if image fails
        })
      )
      : of(null);
  }
}
