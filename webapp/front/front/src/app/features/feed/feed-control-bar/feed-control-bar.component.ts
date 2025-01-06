import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HateoasLinksService, UserSessionService } from '@core/index';
import { User, LinkKey } from '@shared/index';

@Component({
  selector: 'app-feed-control-bar',
  templateUrl: './feed-control-bar.component.html',
})
export class FeedControlBarComponent implements OnInit {
  // CHANNELS
  feedChannelUrl: string;
  announcementsChannelUrl: string;
  complaintsChannelUrl: string;

  channelClass: string = '';
  channel: string;

  // STATUSES
  latestUrl: string;
  hotUrl: string;
  trendingUrl: string;

  statusClass: string = '';
  status: string;

  currentUser: User;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private linkService: HateoasLinksService,
    private userSessionService: UserSessionService
  ) {}

  ngOnInit(): void {
    this.userSessionService.getCurrentUser().subscribe((user: User) => {
      this.currentUser = user;
    });

    this.latestUrl = this.linkService.getLink(LinkKey.NONE_POST_STATUS);
    this.hotUrl = this.linkService.getLink(LinkKey.HOT_POST_STATUS);
    this.trendingUrl = this.linkService.getLink(LinkKey.TRENDING_POST_STATUS);

    this.feedChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_FEED_CHANNEL
    );
    this.announcementsChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL
    );
    this.complaintsChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_COMPLAINTS_CHANNEL
    );

    this.route.queryParams.subscribe((params) => {
      this.status = params['withStatus'];
      this.channel = params['inChannel'];
      this.updateStatusClass();
      this.updateChannelClass();
    });
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

  updateStatusClass() {
    if (this.status === this.latestUrl) {
      this.statusClass = 'Latest';
    } else if (this.status === this.hotUrl) {
      this.statusClass = 'Hot';
    } else if (this.status === this.trendingUrl) {
      this.statusClass = 'Trending';
    }
  }

  changeStatusToLatest(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        withStatus: this.latestUrl,
        inChannel: this.channel, // Preserve the current channel
      },
      queryParamsHandling: 'merge', // Merge with existing query params
    });
    this.updateStatusClass();
  }

  changeStatusToHot(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        withStatus: this.hotUrl,
        inChannel: this.channel, // Preserve the current channel
      },
      queryParamsHandling: 'merge', // Merge with existing query params
    });
    this.updateStatusClass();
  }

  changeStatusToTrending(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        withStatus: this.trendingUrl,
        inChannel: this.channel, // Preserve the current channel
      },
      queryParamsHandling: 'merge', // Merge with existing query params
    });
    this.updateStatusClass();
  }

  publishInChannel(): void {
    this.router.navigate(['/posts/new'], {
      queryParams: { inChannel: this.channel },
    });
  }
}
