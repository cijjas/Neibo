import { Component, OnInit, OnDestroy } from '@angular/core';
import { UserService, User, Roles } from '@shared/index';
import { SafeUrl } from '@angular/platform-browser';
import {
  ImageService,
  AuthService,
  UserSessionService,
  ToastService,
  // PreferencesService,
} from '@core/index';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';

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
  currentUserRole: Roles;
  private subscriptions = new Subscription();

  constructor(
    private userService: UserService,
    private userSessionService: UserSessionService,
    private imageService: ImageService,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router // private preferencesService: PreferencesService
  ) {}

  ngOnInit(): void {
    const userSub = this.userSessionService
      .getCurrentUser()
      .subscribe((user: User | null) => {
        if (user) {
          this.currentUser = user;
          this.darkMode = !!user.darkMode;
          this.language = user.language === 'SPANISH' ? 'es' : 'en';
          this.loadProfileImage(user.image);
        }
      });
    this.subscriptions.add(userSub);
    this.currentUserRole = this.userSessionService.getCurrentRole();

    switch (this.currentUserRole) {
      case Roles.ADMINISTRATOR:
        this.theme = 'admin';
        break;
      case Roles.WORKER:
        this.theme = 'services';
        break;
      default:
        this.theme = 'default';
    }
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

  toggleDarkMode(): void {
    // const currentDarkMode =
    //   document.documentElement.classList.contains('dark-mode');
    // this.preferencesService.setDarkMode(!currentDarkMode);
  }

  toggleLanguage(): void {
    if (this.currentUser) {
      this.userService
        .toggleLanguage(this.currentUser)
        .subscribe((updatedUser) => {
          this.userSessionService.setUserInformation(updatedUser);
        });
    }
  }

  onProfilePictureSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files[0]) {
      const file = fileInput.files[0];
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
        .subscribe((updatedUser) => {
          this.userSessionService.setUserInformation(updatedUser);
          this.loadProfileImage(updatedUser.image);
        });
    }
  }

  logout(): void {
    this.authService.logout();
    this.toastService.showToast('Successfully logged out.', 'success');
  }
}
