import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, switchMap } from 'rxjs/operators';
import { HateoasLinksService } from '../../shared/services/index.service';
import { of } from 'rxjs';

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
    // Subscribe to query parameters to update the channelClass
    this.route.queryParams.subscribe((params) => {
      this.channel = params['SPAInChannel'];
      this.updateChannelClass();
    });
  }

  // This method updates the channelClass based on the current SPAInChannel param
  updateChannelClass(): void {
    const defaultChannel = this.linkService.getLink('neighborhood:feedChannel');
    if (!this.channel) {
      this.channel = defaultChannel; // Default to Feed if channel is not set
    }

    if (this.channel === this.feedChannelUrl) {
      this.channelClass = 'Feed';
    } else if (this.channel === this.announcementsChannelUrl) {
      this.channelClass = 'Announcements';
    } else if (this.channel === this.complaintsChannelUrl) {
      this.channelClass = 'Complaints';
    }
  }


  changeChannelToComplaints(): void {
    this.router.navigate(['/posts'], {
      relativeTo: this.route,
      queryParams: { SPAInChannel: this.complaintsChannelUrl },
    }).then(() => {
      this.updateChannelClass();
    });
  }

  changeChannelToAnnouncements(): void {
    this.router.navigate(['/posts'], {
      relativeTo: this.route,
      queryParams: { SPAInChannel: this.announcementsChannelUrl },
    }).then(() => {
      this.updateChannelClass();

    });
  }

  changeChannelToFeed(): void {
    this.router.navigate(['/posts'], {
      relativeTo: this.route,
      queryParams: { SPAInChannel: this.feedChannelUrl },
    }).then(() => {
      this.updateChannelClass();
    });
  }


}
