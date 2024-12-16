import { Component, OnInit, OnDestroy } from '@angular/core';
import { HateoasLinksService, UserService, UserSessionService } from '../../shared/services/index.service';
import { User } from '../../shared/models/index';
import { SafeUrl } from '@angular/platform-browser';
import { ImageService } from '../../shared/services/core/image.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'user-profile-widget',
  templateUrl: './user-profile-widget.component.html',
})
export class UserProfileWidgetComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  profileImageSafeUrl: SafeUrl | null = null;
  private subscriptions = new Subscription();

  constructor(
    private userService: UserService,
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
