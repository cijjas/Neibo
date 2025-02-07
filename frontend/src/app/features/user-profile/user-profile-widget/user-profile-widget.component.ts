import { Component, OnInit, OnDestroy } from '@angular/core';
import { HateoasLinksService, UserSessionService } from '@core/index';
import { LinkKey, User, ImageService } from '@shared/index';
import { SafeUrl, Title } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../../../environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { AppTitleKeys } from '@shared/constants/app-titles';

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
    private router: Router,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    const title = this.translate.instant(AppTitleKeys.USER_PROFILE_PAGE);
    this.titleService.setTitle(title);

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
