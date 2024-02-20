import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Post } from '../../shared/models/post';
import { PostService } from '../../shared/services/post.service';
import { HttpErrorResponse } from '@angular/common/http';
import { PostStatus } from '../../shared/models/postStatus';
import { Channel } from '../../shared/models/channel';
import {StorageService} from "../../shared/services/storage.service";
import {UserDto} from "../../shared/models/user";
import {LoggedInService} from "../../shared/services/loggedIn.service";
import {combineLatest, of, switchMap, take} from "rxjs";

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
  ) {}

  ngOnInit() {
    // Subscribe to query parameters
    combineLatest([
      this.route.queryParams,
      this.loggedInService.getLoggedUserId(),
      this.loggedInService.getLoggedUserNeighborhoodId(),
    ]).pipe(
        switchMap(([queryParams, userId, neighborhoodId]) => {
          // Check if values are available
          if (userId !== null && neighborhoodId !== null) {
            const channel = queryParams['channel'] || 'Feed';
            const tags = queryParams['tag'] ? [].concat(queryParams['tag']) : [];
            const postStatus = queryParams['postStatus'] || 'none';
            const page = queryParams['page'] || 1;
            const size = queryParams['size'] || 10;

            console.log("calling with", neighborhoodId, channel, tags, postStatus, userId, page, size);
            this.getPosts(neighborhoodId, channel, tags, postStatus, userId, page, size);
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


  private getPosts(neighborhoodId: number, channel: Channel, tags: string[], postStatus: PostStatus, userId: number, page: number, size: number): void {
    this.postService.getPosts(neighborhoodId, channel, tags, postStatus, userId, page, size)
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
