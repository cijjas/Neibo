
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HateoasLinksService, UserSessionService } from '@core/index';
import { User } from '@shared/index';
import { switchMap } from 'rxjs';

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
  ) { }

  ngOnInit(): void {
    this.userSessionService.getCurrentUser().subscribe((user: User) => { this.currentUser = user; });

    this.latestUrl = this.linkService.getLink('neighborhood:nonePostStatus');
    this.hotUrl = this.linkService.getLink('neighborhood:hotPostStatus');
    this.trendingUrl = this.linkService.getLink('neighborhood:trendingPostStatus');

    this.feedChannelUrl = this.linkService.getLink('neighborhood:feedChannel');
    this.announcementsChannelUrl = this.linkService.getLink('neighborhood:announcementsChannel');
    this.complaintsChannelUrl = this.linkService.getLink('neighborhood:complaintsChannel');

    this.route.queryParams.subscribe((params) => {
      this.status = params['SPAWithStatus'];
      this.channel = params['SPAInChannel']
      this.updateStatusClass();
      this.updateChannelClass()
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
    }
    else if (this.status === this.hotUrl) {
      this.statusClass = 'Hot';
    }
    else if (this.status === this.trendingUrl) {
      this.statusClass = 'Trending';
    }
  }

  changeStatusToLatest(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { SPAWithStatus: this.latestUrl },
    })
    this.updateStatusClass()
  }

  changeStatusToHot(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { SPAWithStatus: this.hotUrl },
    })
    this.updateStatusClass()

  }

  changeStatusToTrending(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { SPAWithStatus: this.trendingUrl },
    })
    this.updateStatusClass()
  }

  publishInChannel(): void {
    this.router.navigate(['posts/new'], {
      queryParams: { SPAInChannel: this.channel }
    });
  }



}
