import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-left-column',
  templateUrl: './left-column.component.html',
  styleUrls: ['../../app.component.css']
})
export class LeftColumnComponent {
  channelClass: string = '';

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.url.subscribe(segments => {
      const path = segments.length > 0 ? segments[0].path : '';

      this.route.queryParams.subscribe(queryParams => {
        this.channelClass = this.determineChannelClass(path, queryParams);
      });
    });
  }

  private determineChannelClass(path: string, queryParams: any): string {
    // Check path and queryParams to determine the channel class
    if (path === '') {
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
