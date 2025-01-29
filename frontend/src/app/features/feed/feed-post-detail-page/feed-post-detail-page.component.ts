import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';

import { Comment, Post, CommentService, LikeService } from '@shared/index';
import { UserSessionService } from '@core/index';

@Component({
  selector: 'app-feed-post-detail-page',
  templateUrl: './feed-post-detail-page.component.html',
})
export class FeedPostDetailPageComponent implements OnInit {
  public post!: Post;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    // Get resolved post from route data
    this.route.data.subscribe(({ post }) => {
      if (!post) {
        console.error('Post not found or failed to resolve');
        return;
      }
      this.post = post;
    });
  }
}
