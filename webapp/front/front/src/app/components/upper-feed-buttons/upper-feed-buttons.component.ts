import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-upper-feed-buttons',
  templateUrl: './upper-feed-buttons.component.html',
  styleUrls: ['../../app.component.css']
})
export class UpperFeedButtonsComponent {

  postStatus: string = 'none';

  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.queryParams.subscribe(queryParams => {
      this.postStatus = queryParams['postStatus'] || 'none';
    });
  }

}
