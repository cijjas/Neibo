import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, of, switchMap } from "rxjs";
import { HttpErrorResponse } from '@angular/common/http';

import { Post } from '../../shared/models/index';
import { PostService, UserSessionService } from '../../shared/services/index.service';
import { HateoasLinksService } from '../../shared/services/core/link.service';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['../../app.component.css']
})
export class FeedComponent implements OnInit {
  public postList: Post[] = [];
  public loading: boolean = true;

  constructor(
    private postService: PostService,
    private route: ActivatedRoute,
    private linkService: HateoasLinksService,
    private userSessionService: UserSessionService
  ) { }

  ngOnInit(): void {
    const postsUrl = this.linkService.getLink('neighborhood:posts')

    this.route.queryParams
      .pipe(
        switchMap((queryParams) => {
          const { page, size, inChannel, withTags, withStatus, postedBy } = queryParams;

          return this.postService.getPosts(postsUrl, {
            page: page ? +page : undefined,
            size: size ? +size : undefined,
            inChannel,
            withTags: withTags ? withTags.split(',') : undefined,
            withStatus,
            postedBy,
          });
        })
      )
      .subscribe({
        next: (posts: Post[]) => {
          this.postList = posts;
          this.loading = false;
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error fetching posts:', error);
          this.loading = false;
        }
      });
  }
}
