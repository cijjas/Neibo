
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HateoasLinksService, UserSessionService } from '../../shared/services/index.service';
import { User } from '../../shared/models/user.model';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-upper-feed-buttons',
  templateUrl: './upper-feed-buttons.component.html',
})
export class UpperFeedButtonsComponent implements OnInit {
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
      queryParamsHandling: 'merge'
    })
    this.updateStatusClass()
  }

  changeStatusToHot(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { SPAWithStatus: this.hotUrl },
      queryParamsHandling: 'merge'
    })
    this.updateStatusClass()

  }

  changeStatusToTrending(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { SPAWithStatus: this.trendingUrl },
      queryParamsHandling: 'merge'
    })
    this.updateStatusClass()
  }

  publishInChannel(): void {
    console.log("hola");
    // go to channel publish
  }


}
