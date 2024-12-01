import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PostService, TagService, HateoasLinksService, UserSessionService } from '../../shared/services/index.service';
import { Tag, Channel } from '../../shared/models/index';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';

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

    // Get query param for channels
    this.feedChannelUrl = this.linkService.getLink('neighborhood:feedChannel');
    this.announcementsChannelUrl = this.linkService.getLink('neighborhood:announcementsChannel');
    this.complaintsChannelUrl = this.linkService.getLink('neighborhood:complaintsChannel');

    this.route.queryParams.subscribe((params) => {
      this.channel = params['SPAInChannel'];
      this.updateChannelTitle();
    });
    // this.createPostForm.channel = this.channel

    // Fetch initial data
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
    if (!this.createPostForm.valid) {
      console.error('Form is invalid');
      return;
    }

    const formValue = this.createPostForm.value;
    formValue.channel = this.channel;

    this.userSessionService.getCurrentUser()
      .pipe(
        switchMap(user => {
          formValue.user = user.self;

          // Create tags in parallel and collect URLs
          const createTagObservables = this.tags.map(tag =>
            this.tagService.createTag(tag).pipe(
              catchError(error => {
                console.error(`Error creating tag: ${tag}`, error);
                return of(null); // Continue even if a tag fails
              })
            )
          );

          return forkJoin(createTagObservables).pipe(
            map(tagUrls => tagUrls.filter(url => url !== null)) // Remove null values
          );
        })
      )
      .subscribe({
        next: tagUrls => {
          formValue.tags = tagUrls;

          console.log(formValue);

          // Submit the post
          this.postService.createPost(formValue).subscribe(
            post => {
              this.showSuccessMessage = true;
              setTimeout(() => {
                this.router.navigate(['/posts', this.channel]);
              }, 2000);
            },
            error => {
              console.error('Error creating post:', error);
            }
          );
        },
        error: error => {
          console.error('Error during submission:', error);
        },
        complete: () => console.log('Post submission process completed')
      });
  }


}
