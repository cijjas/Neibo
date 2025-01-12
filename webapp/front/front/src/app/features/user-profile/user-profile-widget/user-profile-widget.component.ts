import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  HateoasLinksService,
  UserSessionService,
  ImageService,
} from '@core/index';
import { LinkKey, User } from '@shared/index';
import { SafeUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-profile-widget',
  templateUrl: './user-profile-widget.component.html',
  standalone: true,
  imports: [CommonModule, RouterModule],
})
export class UserProfileWidgetComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  profileImageSafeUrl: SafeUrl | null = null;
  private subscriptions = new Subscription();

  constructor(
    private userSessionService: UserSessionService,
    private imageService: ImageService,
    private linkService: HateoasLinksService,
    private router: Router // Inject Router to handle navigation
  ) {}

  ngOnInit(): void {
    const userSub = this.userSessionService
      .getCurrentUser()
      .subscribe((user: User | null) => {
        this.currentUser = user;

        if (user?.image) {
          // Check if user and image exist
          const imageSub = this.imageService.fetchImage(user.image).subscribe({
            next: ({ safeUrl }) => {
              this.profileImageSafeUrl = safeUrl;
            },
            error: (error) => {
              console.error('Error fetching profile image:', error);
            },
          });
          this.subscriptions.add(imageSub);
        } else {
          this.profileImageSafeUrl = null; // Reset if no image
        }
      });
    this.subscriptions.add(userSub);
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  navigateToProfile(): void {
    this.router.navigate([
      '/user',
      this.linkService.getLink(LinkKey.USER_SELF),
    ]);
  }
}
