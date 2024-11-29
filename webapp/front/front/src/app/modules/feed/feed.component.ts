import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, of, switchMap } from "rxjs";
import { HttpErrorResponse } from '@angular/common/http';

import { Post } from '../../shared/models/index';
import { PostService, UserSessionService } from '../../shared/services/index.service';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['../../app.component.css']
})
export class FeedComponent implements OnInit {
  public postList: Post[] = [];
  public loading: boolean = false; // Optional: Indicates loading state

  constructor(
    private postService: PostService,
    private route: ActivatedRoute,
    private userSessionService: UserSessionService
  ) { }

  ngOnInit(): void {
    //this.loadFeedData();
  }

  // private loadFeedData(): void {
  //   combineLatest([
  //     this.route.queryParams,
  //     this.userSessionService.getLoggedUser(),
  //   ])
  //     .pipe(
  //       switchMap(([queryParams, user]) => {
  //         if (user) {
  //           const neighborhoodUrn = user.neighborhood;
  //           const channel = queryParams['channel'];
  //           const tags = queryParams['tag'] ? [].concat(queryParams['tag']) : [];
  //           const postStatus = queryParams['postStatus'];
  //           const page = parseInt(queryParams['page'] || '1', 10);
  //           const size = parseInt(queryParams['size'] || '10', 10);

  //           return this.getPosts(neighborhoodUrn, channel, tags, postStatus, page, size);
  //         } else {
  //           console.error('No logged-in user found. Cannot load feed.');
  //           return of([]); // Return an empty array if user is not found
  //         }
  //       })
  //     )
  //     .subscribe({
  //       next: (posts: Post[]) => {
  //         this.postList = posts;
  //         this.loading = false;
  //       },
  //       error: (error: HttpErrorResponse) => {
  //         console.error('Error loading feed data:', error);
  //         this.loading = false;
  //       }
  //     });
  // }

  // private getPosts(
  //   neighborhood: string,
  //   channel: string,
  //   tags: string[],
  //   postStatus: string,
  //   page: number,
  //   size: number
  // ) {
  //   this.loading = true; 
  //   // TODO: add queery params to getPosts
  //   return this.postService.getPosts(neighborhood, page, size);
  // }
}
