import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  AuthService,
  HateoasLinksService,
  UserSessionService,
} from '@core/index';
import { User, LinkKey, Role } from '@shared/index';
import { TranslateService } from '@ngx-translate/core';

export enum FeedStatus {
  LATEST = 'Latest',
  HOT = 'Hot',
  TRENDING = 'Trending',
}

@Component({
  selector: 'app-feed-control-bar',
  templateUrl: './feed-control-bar.component.html',
})
export class FeedControlBarComponent implements OnInit {
  // CHANNELS
  feedChannelUrl: string;
  announcementsChannelUrl: string;
  complaintsChannelUrl: string;

  channelClass: string = 'Announcements';
  channel: string;

  // STATUSES
  private statusMapping: Record<string, FeedStatus>;
  FeedStatus = FeedStatus;
  statusClass: string = '';
  status: FeedStatus;
  latestUrl: string;
  hotUrl: string;
  trendingUrl: string;

  currentUser: User;
  isNotAdmin: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private linkService: HateoasLinksService,
    private userSessionService: UserSessionService,
  ) {}

  ngOnInit(): void {
    this.currentUser = this.userSessionService.getCurrentUserValue();

    this.isNotAdmin =
      this.userSessionService.getCurrentRole() != Role.ADMINISTRATOR;

    this.latestUrl = this.linkService.getLink(LinkKey.NONE_POST_STATUS);
    this.hotUrl = this.linkService.getLink(LinkKey.HOT_POST_STATUS);
    this.trendingUrl = this.linkService.getLink(LinkKey.TRENDING_POST_STATUS);
    this.statusMapping = {
      [this.latestUrl]: FeedStatus.LATEST,
      [this.hotUrl]: FeedStatus.HOT,
      [this.trendingUrl]: FeedStatus.TRENDING,
    };

    this.feedChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_FEED_CHANNEL,
    );
    this.announcementsChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL,
    );
    this.complaintsChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_COMPLAINTS_CHANNEL,
    );

    this.route.queryParams.subscribe(params => {
      const paramStatus = params['withStatus'];
      this.status = this.statusMapping[paramStatus] || FeedStatus.LATEST;
      this.channel = params['inChannel'];
      this.updateStatusClass();
      this.updateChannelClass();
    });
  }

  updateStatusClass() {
    if (this.status === this.latestUrl) {
      this.statusClass = 'Latest';
    } else if (this.status === this.hotUrl) {
      this.statusClass = 'Hot';
    } else if (this.status === this.trendingUrl) {
      this.statusClass = 'Trending';
    }
  }

  updateChannelClass() {
    if (this.channel === this.feedChannelUrl) {
      this.channelClass = 'Feed';
    } else if (this.channel === this.announcementsChannelUrl) {
      this.channelClass = 'Announcements';
    } else if (this.channel === this.complaintsChannelUrl) {
      this.channelClass = 'Complaints';
    }
  }

  changeStatus(newStatus: FeedStatus): void {
    let newUrl = this.latestUrl; // Default to Latest
    if (newStatus === FeedStatus.HOT) {
      newUrl = this.hotUrl;
    } else if (newStatus === FeedStatus.TRENDING) {
      newUrl = this.trendingUrl;
    }

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { withStatus: newUrl },
      queryParamsHandling: 'merge',
    });
  }

  changeStatusToLatest(): void {
    this.changeStatus(FeedStatus.LATEST);
  }

  changeStatusToHot(): void {
    this.changeStatus(FeedStatus.HOT);
  }

  changeStatusToTrending(): void {
    this.changeStatus(FeedStatus.TRENDING);
  }

  publishInChannel(): void {
    this.router.navigate(['/posts/new'], {
      queryParams: { inChannel: this.channel },
    });
  }
}
