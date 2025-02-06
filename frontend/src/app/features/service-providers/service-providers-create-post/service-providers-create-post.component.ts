import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ImageService, ToastService, UserSessionService } from '@core/index';
import { PostService } from '@shared/index';
import { Channel } from '@shared/models';
import { catchError, of, switchMap, take } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { VALIDATION_CONFIG } from '@shared/constants/validation-config';

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
  imagePreviewUrl: string | ArrayBuffer | null = null;
  fileUploadError: string | null = null;

  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private imageService: ImageService,
    private router: Router,
    private route: ActivatedRoute,
    private userSessionService: UserSessionService,
    private toastService: ToastService,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    // Build form
    this.createWrokerPostForm = this.fb.group({
      title: ['', Validators.required],
      body: ['', Validators.required],
      // Keep image optional or add a custom validator if you want
      imageFile: [null, VALIDATION_CONFIG.imageValidator],
      channel: [''],
      user: [''],
    });

    // Listen to route queryParams
    this.route.queryParams.subscribe(params => {
      this.channel = params['inChannel'];
      this.workerId = params['forWorker'];
    });
  }

  // ------------------ GETTERS ------------------
  get imageFileControl() {
    return this.createWrokerPostForm.get('imageFile');
  }

  // ------------------ DRAG & DROP + PREVIEW ------------------
  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    // Optionally add a highlight class in CSS
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
    this.createWrokerPostForm.patchValue({ imageFile: file });
    this.imageFileControl?.markAsTouched();

    // If you want to do validation checks, do them here
    // If there's an error, remove the preview:
    // if (this.imageFileControl?.errors) {
    //   this.imagePreviewUrl = null;
    //   return;
    // }

    // Else show preview
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreviewUrl = reader.result;
    };
    reader.readAsDataURL(file);
  }

  // ------------------ ON FILE INPUT CHANGE ------------------
  onFileChange(event: any) {
    const file = event.target.files?.[0];
    if (!file) return;

    // Patch into form
    this.createWrokerPostForm.patchValue({ imageFile: file });
    this.imageFileControl?.markAsTouched();

    // If invalid, remove preview (only if you have a validator)
    // if (this.imageFileControl?.errors) {
    //   this.imagePreviewUrl = null;
    //   return;
    // }

    // Show preview
    const reader = new FileReader();
    reader.onload = e => (this.imagePreviewUrl = reader.result);
    reader.readAsDataURL(file);
  }

  // ------------------ REMOVE IMAGE ------------------
  removeImage(): void {
    this.imagePreviewUrl = null;

    // Reset the form control
    const imageControl = this.createWrokerPostForm.get('imageFile');
    if (imageControl) {
      imageControl.patchValue(null);
      imageControl.setErrors(null);
      imageControl.markAsPristine();
      imageControl.markAsUntouched();
    }

    // Clear the native <input type="file">
    const input = document.getElementById('images') as HTMLInputElement;
    if (input) {
      input.value = '';
    }
  }

  // ------------------ FORM SUBMIT ------------------
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
        switchMap(user => {
          formValue.user = user.self;
          formValue.channel = this.channel;

          // Upload image if present
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
          this.toastService.showToast(
            this.translate.instant(
              'SERVICE-PROVIDERS-CREATE-POST.YOUR_POST_WAS_CREATED_SUCCESSFULLY',
            ),
            'success',
          );
          // Navigate to the worker's posts
          this.router.navigate(['/services', 'profiles', this.workerId], {
            queryParams: { tab: 'posts' },
          });
        },
        error: error => {
          this.toastService.showToast(
            this.translate.instant(
              'SERVICE-PROVIDERS-CREATE-POST.THERE_WAS_A_PROBLEM_CREATING_YOUR_POST',
            ),
            'error',
          );
          console.error('Error creating post:', error);
        },
      });
  }

  /**
   * Uploads the image if present, returns image URL or null.
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
}
