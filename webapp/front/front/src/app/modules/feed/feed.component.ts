import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Post } from '../../shared/models/post';
import { PostService } from '../../shared/services/post.service';
import { HttpErrorResponse } from '@angular/common/http';
import { PostStatus } from '../../shared/models/postStatus';
import { StorageService } from "../../shared/services/storage.service";
import { UserDto } from "../../shared/models/user";
import { LoggedInService } from "../../shared/services/loggedIn.service";
import { combineLatest, of, switchMap, take } from "rxjs";

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['../../app.component.css']
})
export class FeedComponent implements OnInit {
  public postList: Post[] = [];

  constructor(
    private postService: PostService,
    private route: ActivatedRoute,
    private storageService: StorageService,
    private loggedInService: LoggedInService
  ) { }

  ngOnInit() {
    // Subscribe to query parameters
    combineLatest([
      this.route.queryParams,
      this.loggedInService.getLoggedUserURN(),
      this.loggedInService.getLoggedUserNeighborhoodURN(),
    ]).pipe(
      switchMap(([queryParams, user, neighborhood]) => {
        // Check if values are available
        if (user !== null && neighborhood !== null) {
          const channel = queryParams['channel'];
          const tags = queryParams['tag'] ? [].concat(queryParams['tag']) : [];
          const postStatus = queryParams['postStatus'];
          const page = queryParams['page'] || 1;
          const size = queryParams['size'] || 10;

          this.getPosts(neighborhood, channel, tags, postStatus, user, page, size);
          return of(null); // You can emit any value here since you've already handled the logic
        } else {
          // Handle the case when userId or neighborhoodId is not available
          console.error('UserId or neighborhoodId is not available.');
          return of(null); // You can emit any value here since you've handled the error
        }
      }),
      take(1) // Take only one emission to avoid continuous subscription
    ).subscribe();
  }

  private getPosts(neighborhood: string, channel: string, tags: string[], postStatus: string, user: string, page: number, size: number): void {
    this.postService.getPosts(neighborhood, channel, tags, postStatus, user, page, size)
      .subscribe({
        next: (posts: Post[]) => {
          this.postList = posts;
        },
        error: (error) => {
          console.error('Error getting posts:', error);
        }
      });
  }
}
