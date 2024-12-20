import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { HateoasLinksService } from '../../shared/services/index.service';

@Component({
  selector: 'app-left-column',
  templateUrl: './left-column.component.html',
})
export class LeftColumnComponent implements OnInit {
  channelClass: string = '';
  feedChannelUrl: string;
  announcementsChannelUrl: string;
  complaintsChannelUrl: string;
  channel: string;

  constructor(
    private route: ActivatedRoute,
    private linkService: HateoasLinksService,
    private router: Router
  ) { }

  ngOnInit() {
    // Fetch channel URLs from the link service
    this.feedChannelUrl = this.linkService.getLink('neighborhood:feedChannel');
    this.announcementsChannelUrl = this.linkService.getLink('neighborhood:announcementsChannel');
    this.complaintsChannelUrl = this.linkService.getLink('neighborhood:complaintsChannel');

    // Subscribe to router events to track URL changes
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.updateChannelClass();
      });

    // Initial call to set the channel class
    this.updateChannelClass();
  }

  // Update channelClass based on the current URL
  updateChannelClass(): void {
    const currentUrl = this.router.url;

    if (currentUrl.includes('/posts') && this.route.snapshot.queryParams['SPAInChannel'] === this.feedChannelUrl) {
      this.channelClass = 'Feed';
    } else if (currentUrl.includes('/posts') && this.route.snapshot.queryParams['SPAInChannel'] === this.announcementsChannelUrl) {
      this.channelClass = 'Announcements';
    } else if (currentUrl.includes('/posts') && this.route.snapshot.queryParams['SPAInChannel'] === this.complaintsChannelUrl) {
      this.channelClass = 'Complaints';
    } else if (currentUrl.startsWith('/marketplace')) {
      this.channelClass = 'Marketplace';
    } else if (currentUrl.startsWith('/services')) {
      this.channelClass = 'Services';
    } else if (currentUrl.startsWith('/reservations')) {
      this.channelClass = 'Reservations';
    } else if (currentUrl.startsWith('/information')) {
      this.channelClass = 'Information';
    } else {
      this.channelClass = '';
    }
  }

  // Navigation methods
  changeChannelToComplaints(): void {
    this.router.navigate(['/posts'], {
      relativeTo: this.route,
      queryParams: { SPAInChannel: this.complaintsChannelUrl },
    }).then(() => this.updateChannelClass());
  }

  changeChannelToAnnouncements(): void {
    this.router.navigate(['/posts'], {
      relativeTo: this.route,
      queryParams: { SPAInChannel: this.announcementsChannelUrl },
    }).then(() => this.updateChannelClass());
  }

  changeChannelToFeed(): void {
    this.router.navigate(['/posts'], {
      relativeTo: this.route,
      queryParams: { SPAInChannel: this.feedChannelUrl },
    }).then(() => this.updateChannelClass());
  }
}
