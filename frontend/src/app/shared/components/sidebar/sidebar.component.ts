import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import {
  AuthService,
  HateoasLinksService,
  UserSessionService,
} from '@core/index';
import { LinkKey, Role } from '@shared/models';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent implements OnInit {
  userRole: Role | null = null; // Role of the logged-in user
  workerId: string | null = '';
  channelClass: string = ''; // Active channel class
  public Roles = Role; // Expose Roles enum to the template

  constructor(
    private linkService: HateoasLinksService,
    private userSessionService: UserSessionService,
    private router: Router,
  ) {}

  ngOnInit() {
    this.userRole = this.userSessionService.getCurrentRole();
    this.workerId = this.linkService.getLink(LinkKey.USER_WORKER);

    // Listen for navigation changes to update the channel
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => this.updateChannelClass());

    // Initial update of the channel class
    this.updateChannelClass();
  }

  updateChannelClass(): void {
    const currentUrl = this.router.url;

    // 1) Check more specific routes first
    if (currentUrl.startsWith('/services/profiles')) {
      this.channelClass = 'Profile';
    } else if (currentUrl.startsWith('/services/join-neighborhoods')) {
      this.channelClass = 'Neighborhoods';
    }
    // 2) Then check the more generic routes
    else if (currentUrl.startsWith('/services')) {
      this.channelClass = 'Services';
    } else if (currentUrl.startsWith('/marketplace')) {
      this.channelClass = 'Marketplace';
    } else if (currentUrl.startsWith('/amenities')) {
      this.channelClass = 'Reservations';
    } else if (currentUrl.startsWith('/information')) {
      this.channelClass = 'Information';
    }
    // 3) Handle /posts routes with query parameters
    else if (currentUrl.includes('/posts')) {
      const queryParams = this.router.routerState.snapshot.root.queryParams;
      if (
        queryParams['inChannel'] ===
        this.linkService.getLink(LinkKey.NEIGHBORHOOD_FEED_CHANNEL)
      ) {
        this.channelClass = 'Feed';
      } else if (
        queryParams['inChannel'] ===
          this.linkService.getLink(
            LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL,
          ) ||
        !queryParams['inChannel']
      ) {
        this.channelClass = 'Announcements';
      } else if (
        queryParams['inChannel'] ===
        this.linkService.getLink(LinkKey.NEIGHBORHOOD_COMPLAINTS_CHANNEL)
      ) {
        this.channelClass = 'Complaints';
      } else {
        this.channelClass = '';
      }
    } else {
      this.channelClass = '';
    }
  }

  changeChannelToComplaints(): void {
    this.router
      .navigate(['/posts'], {
        queryParams: {
          inChannel: this.linkService.getLink(
            LinkKey.NEIGHBORHOOD_COMPLAINTS_CHANNEL,
          ),
          withStatus: null, // Reset status filter
        },
      })
      .then(() => this.updateChannelClass());
  }

  changeChannelToAnnouncements(): void {
    this.router
      .navigate(['/posts'], {
        queryParams: {
          inChannel: this.linkService.getLink(
            LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL,
          ),
          withStatus: null, // Reset status filter
        },
      })
      .then(() => this.updateChannelClass());
  }

  changeChannelToFeed(): void {
    this.router
      .navigate(['/posts'], {
        queryParams: {
          inChannel: this.linkService.getLink(
            LinkKey.NEIGHBORHOOD_FEED_CHANNEL,
          ),
          withStatus: null, // Reset status filter
        },
      })
      .then(() => this.updateChannelClass());
  }
}
