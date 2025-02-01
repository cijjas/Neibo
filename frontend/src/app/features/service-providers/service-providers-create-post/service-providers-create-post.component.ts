import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
  AuthService,
  HateoasLinksService,
  ImageService,
  ToastService,
  UserSessionService,
} from '@core/index';
import { PostService } from '@shared/index';
import { Channel } from '@shared/models';
import { catchError, of, switchMap, take } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-service-providers-create-post',
  templateUrl: './service-providers-create-post.component.html',
})
export class ServiceProvidersCreatePostComponent {
  // Reactive form
  createWrokerPostForm: FormGroup;

  // Channel data
  channelList: Channel[] = [];
  channel: string;
  workerId: string;

  // For image preview, etc.
  imagePreviewUrl: string | ArrayBuffer;
  fileUploadError: string;

  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private imageService: ImageService,
    private router: Router,
    private route: ActivatedRoute,
    private userSessionService: UserSessionService,
    private toastService: ToastService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    // Build form
    this.createWrokerPostForm = this.fb.group({
      title: ['', Validators.required],
      body: ['', Validators.required],
      imageFile: [null],
      channel: [''],
      user: [''],
    });

    // Listen to route queryParams for channel
    this.route.queryParams.subscribe((params) => {
      this.channel = params['inChannel'];
      this.workerId = params['forWorker'];
    });
  }

  // ---- IMAGE HANDLING ----

  onFileChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.createWrokerPostForm.patchValue({ imageFile: file });

      // Preview
      const reader = new FileReader();
      reader.onload = (e) => (this.imagePreviewUrl = reader.result);
      reader.readAsDataURL(file);
    }
  }

  // ---- FORM SUBMIT ----

  onSubmit(): void {
    if (this.createWrokerPostForm.invalid) {
      console.error('Form is invalid');
      return;
    }

    const formValue = { ...this.createWrokerPostForm.value };

    // Get the current user and create the post
    this.userSessionService
      .getCurrentUser()
      .pipe(
        take(1),
        switchMap((user) => {
          formValue.user = user.self;
          formValue.channel = this.channel;
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
        next: (response) => {
          this.toastService.showToast(
            this.translate.instant('SERVICE-PROVIDERS-CREATE-POST.YOUR_POST_WAS_CREATED_SUCCESSFULLY'),
            'success'
          );
          // Navigate the workerrs posts
          this.router.navigate(['/services', 'profile', this.workerId], {
            queryParams: { tab: 'posts' },
          });
        },
        error: (error) => {
          this.toastService.showToast(
            this.translate.instant('SERVICE-PROVIDERS-CREATE-POST.THERE_WAS_A_PROBLEM_CREATING_YOUR_POST'),
            'error'
          );
          console.error('Error creating post:', error);
        },
      });
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
