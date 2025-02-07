import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';

import { Comment, Post, CommentService, LikeService } from '@shared/index';
import { UserSessionService } from '@core/index';
import { AppTitleKeys } from '@shared/constants/app-titles';
import { TranslateService } from '@ngx-translate/core';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-feed-post-detail-page',
  templateUrl: './feed-post-detail-page.component.html',
})
export class FeedPostDetailPageComponent implements OnInit {
  public post!: Post;

  constructor(
    private route: ActivatedRoute,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    // Get resolved post from route data
    this.route.data.subscribe(({ post }) => {
      if (!post) {
        console.error('Post not found or failed to resolve');
        return;
      }
      this.post = post;

      const title = this.translate.instant(AppTitleKeys.FEED_POST_DETAIL_PAGE, {
        postTitle: this.post.title,
      });
      this.titleService.setTitle(title);
    });
  }
}
