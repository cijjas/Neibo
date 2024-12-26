import { Component, OnInit, OnDestroy } from '@angular/core';
import { HateoasLinksService, UserSessionService, ImageService } from '@core/index';
import { User } from '@shared/index';
import { SafeUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-profile-widget',
  templateUrl: './user-profile-widget.component.html',
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
  ) { }

  ngOnInit(): void {
    const userSub = this.userSessionService.getCurrentUser().subscribe((user: User) => {
      this.currentUser = user;

      const imageSub = this.imageService.fetchImage(user?.image).subscribe(({ safeUrl }) => {
        this.profileImageSafeUrl = safeUrl;
      });
      this.subscriptions.add(imageSub);
    });
    this.subscriptions.add(userSub);
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  navigateToProfile(): void {
    this.router.navigate(['/user', this.linkService.getLink('user:self')]);
  }
}
