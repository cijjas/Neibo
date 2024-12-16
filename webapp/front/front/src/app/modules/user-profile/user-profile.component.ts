import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService, UserService, UserSessionService } from '../../shared/services/index.service';
import { User } from '../../shared/models/index';
import { SafeUrl } from '@angular/platform-browser';
import { ImageService } from '../../shared/services/core/image.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'user-profile',
  templateUrl: './user-profile.component.html',
})
export class UserProfileComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  profileImageSafeUrl: SafeUrl | null = null;
  darkMode: boolean = false;
  language: string = 'en';

  private subscriptions = new Subscription();

  constructor(
    private userService: UserService,
    private userSessionService: UserSessionService,
    private imageService: ImageService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    const userSub = this.userSessionService.getCurrentUser().subscribe((user: User | null) => {
      if (user) {
        this.currentUser = user;
        this.darkMode = !!user.darkMode;
        this.language = user.language === 'SPANISH' ? 'es' : 'en';
        this.loadProfileImage(user.image);
      }
    });
    this.subscriptions.add(userSub);
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  loadProfileImage(imageUrl: string): void {
    const imageSub = this.imageService.fetchImage(imageUrl).subscribe(({ safeUrl }) => {
      this.profileImageSafeUrl = safeUrl;
    });
    this.subscriptions.add(imageSub);
  }

  toggleDarkMode(): void {
    if (this.currentUser) {
      this.userService.toggleDarkMode(this.currentUser).subscribe((updatedUser) => {
        this.userSessionService.setUserInformation(updatedUser);
      });
    }
  }

  toggleLanguage(): void {
    if (this.currentUser) {
      this.userService.toggleLanguage(this.currentUser).subscribe((updatedUser) => {
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
      this.userService.uploadProfilePicture(this.currentUser, file).subscribe((updatedUser) => {
        this.userSessionService.setUserInformation(updatedUser);
        this.loadProfileImage(updatedUser.image);
      });
    }
  }

  logout(): void {
    this.userSessionService.logout();
    this.authService.logout();
    this.router.navigate(['/login']); // Redirect to the login page
  }
}
