import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PostService, TagService, HateoasLinksService, UserSessionService, ImageService } from '../../shared/services/index.service';
import { Tag, Channel } from '../../shared/models/index';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, combineLatest, forkJoin, map, Observable, of, switchMap } from 'rxjs';

@Component({
  selector: 'app-create-post',
  templateUrl: './create-post.component.html',
})
export class CreatePostComponent implements OnInit {
  createPostForm: FormGroup;
  loggedUser = { darkMode: false, role: 'USER' }; // Replace with actual logged-in user data
  channelList: Channel[] = []; // Populate this with actual channel data
  tagList: Tag[] = []; // Populate this with actual tag data
  tags: string[] = [];
  imagePreviewUrl: string | ArrayBuffer;
  showSuccessMessage = false;
  fileUploadError: string;

  // CHANNELS
  feedChannelUrl: string;
  announcementsChannelUrl: string;
  complaintsChannelUrl: string;

  title: string = '';
  channel: string;


  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private tagService: TagService,
    private imageService: ImageService,
    private linkService: HateoasLinksService,
    private router: Router,
    private route: ActivatedRoute,
    private userSessionService: UserSessionService
  ) { }

  ngOnInit(): void {
    // Initialize the form
    this.createPostForm = this.fb.group({
      title: ['', Validators.required],
      body: ['', Validators.required],
      imageFile: [null],
      tags: [[]],
      channel: [''],
      user: ['']
    });

    // Get Channel Query Param
    this.feedChannelUrl = this.linkService.getLink('neighborhood:feedChannel');
    this.announcementsChannelUrl = this.linkService.getLink('neighborhood:announcementsChannel');
    this.complaintsChannelUrl = this.linkService.getLink('neighborhood:complaintsChannel');

    this.route.queryParams.subscribe((params) => {
      this.channel = params['SPAInChannel'];
      this.updateChannelTitle();
    });

    // Fetch Tags
    this.fetchTags();
  }

  // ------------------------- UI Functions
  updateChannelTitle() {
    if (this.channel === this.feedChannelUrl) {
      this.title = 'Create Post';
    } else if (this.channel === this.announcementsChannelUrl) {
      this.title = 'Create Announcement';
    } else if (this.channel === this.complaintsChannelUrl) {
      this.title = 'Create Complaint';
    }
  }

  fetchTags() {
    const tagsUrl = this.linkService.getLink('neighborhood:tags');
    this.tagService.getTags(tagsUrl).subscribe(tags => {
      this.tagList = tags;
    });
  }


  onFileChange(event) {
    const file = event.target.files[0];
    if (file) {
      this.createPostForm.patchValue({
        imageFile: file
      });

      // File Preview
      const reader = new FileReader();
      reader.onload = e => this.imagePreviewUrl = reader.result;
      reader.readAsDataURL(file);
    }
  }

  onTagInput(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      const input = event.target as HTMLInputElement;
      const value = input.value.trim();
      if (value && !this.tags.includes(value)) {
        this.tags.push(value);
      }
      input.value = '';
      event.preventDefault();
    }
  }

  addTag(tagName: string) {
    if (!this.tags.includes(tagName)) {
      this.tags.push(tagName);
    }
  }

  removeTag(tag: string) {
    this.tags = this.tags.filter(t => t !== tag);
  }

  // ------------------------- Creation Functions

  onSubmit() {
    if (this.createPostForm.invalid) {
      console.error('Form is invalid');
      return;
    }

    const formValue = { ...this.createPostForm.value, channel: this.channel };

    this.userSessionService.getCurrentUser()
      .pipe(
        switchMap(user => {
          formValue.user = user.self;

          return combineLatest([
            this.createTagsObservable(),
            this.createImageObservable(formValue.imageFile)
          ]);
        }),
        switchMap(([tagUrls, imageUrl]) => {
          formValue.tags = tagUrls.filter(tag => tag !== null); // Filter out null tags
          if (imageUrl) formValue.image = imageUrl;

          return this.postService.createPost(formValue);
        })
      )
      .subscribe({
        next: () => {
          this.showSuccessMessage = true;
          console.log("YENDO")
          this.router.navigate(['/posts'], {
            queryParams: { SPAInChannel: this.channel }
          });
        },
        error: error => console.error('Error creating post:', error)
      });
  }

  private createTagsObservable(): Observable<string[]> {
    return forkJoin(
      this.tags.map(tag =>
        this.tagService.createTag(tag).pipe(
          catchError(error => {
            console.error(`Error creating tag: ${tag}`, error);
            return of(null); // Continue even if a tag fails
          })
        )
      )
    );
  }

  private createImageObservable(imageFile: File | null): Observable<string | null> {
    return imageFile
      ? this.imageService.createImage(imageFile).pipe(
        catchError(error => {
          console.error('Error uploading image:', error);
          return of(null); // Continue even if the image fails to upload
        })
      )
      : of(null);
  }
}
