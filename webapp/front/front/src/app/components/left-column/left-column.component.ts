import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-left-column',
  templateUrl: './left-column.component.html',
  styleUrls: ['../../app.component.css']
})
export class LeftColumnComponent implements OnInit {
  channelClass: string = '';

  constructor(
      private route: ActivatedRoute,
      private router: Router
  ) {}

  ngOnInit() {
    // Subscribe to changes in route parameters
    this.route.queryParams.subscribe(queryParams => {
      this.channelClass = this.determineChannelClass(this.route.snapshot.routeConfig?.path, queryParams);
    });

    this.router.events.pipe(
        filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.channelClass = this.determineChannelClass(this.route.snapshot.routeConfig?.path, this.route.snapshot.queryParams);
    });
  }

  private determineChannelClass(path: string, queryParams: any): string {
    // Check path and queryParams to determine the channel class
    if (path === 'feed') {
      const channel = queryParams['channel'];
      switch (channel) {
        case 'Announcements':
          return 'Announcements';
        case 'Complaints':
          return 'Complaints';
          // Add more cases as needed
        default:
          return 'Feed';
      }
    } else {
      // Handle other routes without query parameters
      switch (path) {
        case 'marketplace':
          return 'Marketplace';
        case 'services':
          return 'Services';
        case 'reservations':
          return 'Reservations';
        case 'information':
          return 'Information';
        default:
          return '';
      }
    }
  }
}
