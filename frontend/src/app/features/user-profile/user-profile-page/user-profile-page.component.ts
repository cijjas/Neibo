import { Component, OnInit, OnDestroy } from '@angular/core';
import { UserService, User, Role, LinkKey } from '@shared/index';
import { SafeUrl } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
import {
  ImageService,
  AuthService,
  UserSessionService,
  ToastService,
  HateoasLinksService,
} from '@core/index';
import { Subscription } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { FormControl } from '@angular/forms';
import { VALIDATION_CONFIG } from '@shared/constants/validation-config';

@Component({
  selector: 'user-user-profile-page',
  templateUrl: './user-profile-page.component.html',
})
export class UserProfilePageComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  profileImageSafeUrl: SafeUrl | null = null;
  darkMode: boolean = false;
  language: string = 'en';
  theme: 'default' | 'marketplace' | 'services' | 'admin' = 'default';
  currentUserRole: Role;
  private subscriptions = new Subscription();
  environment = environment;

  profileImageControl: FormControl = new FormControl(
    null,
    VALIDATION_CONFIG.imageValidator,
  );

  constructor(
    private userService: UserService,
    private userSessionService: UserSessionService,
    private imageService: ImageService,
    private authService: AuthService,
    private toastService: ToastService,
    private translate: TranslateService,
    private linkService: HateoasLinksService,
  ) {}

  ngOnInit(): void {
    const userSub = this.userSessionService
      .getCurrentUser()
      .subscribe((user: User | null) => {
        if (user) {
          this.currentUser = user;
          this.darkMode = !!user.darkMode;
          this.language =
            user.language === this.linkService.getLink(LinkKey.SPANISH_LANGUAGE)
              ? 'es'
              : 'en';
          this.loadProfileImage(user.image);
        }
      });
    this.subscriptions.add(userSub);
    this.currentUserRole = this.userSessionService.getCurrentRole();

    switch (this.currentUserRole) {
      case Role.ADMINISTRATOR:
        this.theme = 'admin';
        break;
      case Role.WORKER:
        this.theme = 'services';
        break;
      default:
        this.theme = 'default';
    }
  }

  get isSpanish(): boolean {
    return this.language === 'es';
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  loadProfileImage(imageUrl: string): void {
    const imageSub = this.imageService
      .fetchImage(imageUrl)
      .subscribe(({ safeUrl }) => {
        this.profileImageSafeUrl = safeUrl;
      });
    this.subscriptions.add(imageSub);
  }

  toggleLanguage(): void {
    if (this.currentUser) {
      this.userService
        .toggleLanguage(this.currentUser)
        .subscribe(updatedUser => {
          this.userSessionService.setUserInformation(updatedUser);
        });
    }
  }

  /**
   * Called when the user selects a new profile image.
   * The method validates the file using the FormControl’s custom validator.
   */
  onProfilePictureSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files[0]) {
      const file = fileInput.files[0];

      // Set the file on the FormControl so that validation is triggered.
      this.profileImageControl.setValue(file);
      this.profileImageControl.updateValueAndValidity();

      // If the file does not meet the validation requirements, show error messages.
      if (this.profileImageControl.errors) {
        if (this.profileImageControl.errors['fileSize']) {
          this.toastService.showToast(
            `Image size exceeds ${this.profileImageControl.errors['fileSize'].requiredMax}MB.`,
            'error',
          );
        }
        if (this.profileImageControl.errors['fileFormat']) {
          this.toastService.showToast(
            'Invalid image format. Only JPEG, PNG, and GIF are allowed.',
            'error',
          );
        }
        return;
      }

      // If the file passes validation, use FileReader to create a preview and upload.
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.profileImageSafeUrl = e.target.result;
        this.uploadProfilePicture(file);
      };
      reader.readAsDataURL(file);
    }
  }

  uploadProfilePicture(file: File): void {
    if (this.currentUser) {
      this.userService
        .uploadProfilePicture(this.currentUser, file)
        .subscribe(updatedUser => {
          this.userSessionService.setUserInformation(updatedUser);
          this.loadProfileImage(updatedUser.image);
        });
    }
  }

  logout(): void {
    this.authService.logout();
    this.toastService.showToast(
      this.translate.instant('USER-PROFILE-PAGE.SUCCESSFULLY_LOGGED_OUT'),
      'success',
    );
  }
}
