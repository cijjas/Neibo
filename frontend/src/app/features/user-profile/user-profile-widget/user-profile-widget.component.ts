import { Component, OnInit, OnDestroy } from '@angular/core';
import { HateoasLinksService, UserSessionService } from '@core/index';
import { LinkKey, User, ImageService } from '@shared/index';
import { SafeUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from "../../../../environments/environment";

@Component({
  selector: 'app-user-profile-widget',
  templateUrl: './user-profile-widget.component.html',
  standalone: true,
  imports: [CommonModule, RouterModule],
})
export class UserProfileWidgetComponent implements OnInit, OnDestroy {
  environment = environment;
  currentUser: User | null = null;
  profileImageSafeUrl: SafeUrl | null = null;
  private subscriptions = new Subscription();

  constructor(
    private userSessionService: UserSessionService,
    private imageService: ImageService,
    private linkService: HateoasLinksService,
    private router: Router, 
  ) { }

  ngOnInit(): void {
    const userSub = this.userSessionService
      .getCurrentUser()
      .subscribe((user: User | null) => {
        this.currentUser = user;
        this.loadProfileImage(user.image);
      });
    this.subscriptions.add(userSub);
  }

  loadProfileImage(imageUrl: string): void {
    const imageSub = this.imageService
      .fetchImage(imageUrl)
      .subscribe(({ safeUrl }) => {
        this.profileImageSafeUrl = safeUrl;
      });
    this.subscriptions.add(imageSub);
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  navigateToProfile(): void {
    this.router.navigate(['/profile']);
  }
}
