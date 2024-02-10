import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Post } from '../../shared/models/post';
import { PostService } from '../../shared/services/post.service';
import { HttpErrorResponse } from '@angular/common/http';
import { PostStatus } from '../../shared/models/postStatus';
import { Channel } from '../../shared/models/channel';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['../../app.component.css']
})
export class FeedComponent implements OnInit {
  public postList: Post[] = [];

  constructor(private postService: PostService, private route: ActivatedRoute) {}

  ngOnInit() {
    // Subscribe to query parameters
    this.route.queryParams.subscribe(queryParams => {
      const neighborhoodId = 1; // You might get this from somewhere else or set it accordingly
      const channel = queryParams['channel'] || 'Feed'; // Use 'Feed' if 'channel' is not provided
      const tags = queryParams['tag'] ? [].concat(queryParams['tag']) : [];
      const postStatus = queryParams['postStatus'] || 'none';
      const userId = 1;
      const page = queryParams['page'] || 1;
      const size = queryParams['size'] || 10;

      // Call the getPosts function with the extracted parameters
      this.getPosts(neighborhoodId, channel, tags, postStatus, userId, page, size);
    });
  }

  private getPosts(neighborhoodId: number, channel: Channel, tags: string[], postStatus: PostStatus, userId: number, page: number, size: number): void {
    this.postService.getPosts(neighborhoodId, channel, tags, postStatus, userId, page, size)
      .subscribe(
        (posts: Post[]) => {
          this.postList = posts;
        },
        (error: HttpErrorResponse) => {
          // Handle error if needed
        }
      );
  }
}
