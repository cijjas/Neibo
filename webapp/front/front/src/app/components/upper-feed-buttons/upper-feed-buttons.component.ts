
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserSessionService } from '../../shared/services/index.service';
import { User } from '../../shared/models/user.model';

@Component({
  selector: 'app-upper-feed-buttons',
  templateUrl: './upper-feed-buttons.component.html',
})
export class UpperFeedButtonsComponent implements OnInit {
  postStatus: string = 'none';
  currentUser: User | null = null;
  channel = 'Feed'

  constructor(
    private route: ActivatedRoute,
    private userSessionService: UserSessionService
  ) { }

  ngOnInit(): void {
    // Subscribe to query parameters to get 'postStatus'
    this.route.queryParams.subscribe(queryParams => {
      this.postStatus = queryParams['postStatus'] || 'none';
    });

    // Subscribe to current user data
    this.userSessionService.getCurrentUser().subscribe(user => {
      this.currentUser = user;
    });
  }

  publishInChannel(): void {
    console.log("hola");
    // go to channel publish
  }


}
