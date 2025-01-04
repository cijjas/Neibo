import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { HateoasLinksService, UserSessionService } from '@core/index';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent implements OnInit {
  userRole: string | null = ''; // Role of the logged-in user
  userId: string | null = '';   // ID of the logged-in user
  workerId: string | null = '';
  channelClass: string = ''; // Active channel class

  constructor(
    private linkService: HateoasLinksService,
    private userSessionService: UserSessionService,
    private router: Router
  ) { }

  ngOnInit() {
    // Fetch the current user role and ID
    const currentUser = this.userSessionService['currentUserSubject'].value; // Direct synchronous access
    if (currentUser) {
      this.userRole = currentUser.userRole;
      this.userId = currentUser.self; // Assuming `self` contains the user ID

      this.workerId = this.linkService.getLink('user:worker');
    }

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
    if (currentUrl.startsWith('/services/profile')) {
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
      if (queryParams['SPAInChannel'] === this.linkService.getLink('neighborhood:feedChannel')) {
        this.channelClass = 'Feed';
      } else if (queryParams['SPAInChannel'] === this.linkService.getLink('neighborhood:announcementsChannel')) {
        this.channelClass = 'Announcements';
      } else if (queryParams['SPAInChannel'] === this.linkService.getLink('neighborhood:complaintsChannel')) {
        this.channelClass = 'Complaints';
      } else {
        this.channelClass = '';
      }
    } else {
      this.channelClass = '';
    }
  }

  changeChannelToComplaints(): void {
    this.router.navigate(['/posts'], {
      queryParams: { SPAInChannel: this.linkService.getLink('neighborhood:complaintsChannel') },
    }).then(() => this.updateChannelClass());
  }

  changeChannelToAnnouncements(): void {
    this.router.navigate(['/posts'], {
      queryParams: { SPAInChannel: this.linkService.getLink('neighborhood:announcementsChannel') },
    }).then(() => this.updateChannelClass());
  }

  changeChannelToFeed(): void {
    this.router.navigate(['/posts'], {
      queryParams: { SPAInChannel: this.linkService.getLink('neighborhood:feedChannel') },
    }).then(() => this.updateChannelClass());
  }
}
