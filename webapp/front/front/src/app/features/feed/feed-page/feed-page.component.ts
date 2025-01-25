import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of, switchMap } from 'rxjs';
import { catchError, distinctUntilChanged, map } from 'rxjs/operators';

import { PostService, Post, LinkKey } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Component({
  selector: 'app-feed-page',
  templateUrl: './feed-page.component.html',
})
export class FeedPageComponent implements OnInit {
  public postList: Post[] = [];
  public loading: boolean = true;

  currentPage: number = 1;

  pageSize: number = 10;
  totalPages: number = 0;
  channel: string;
  postStatus: string;
  tags: string[] = [];

  constructor(
    private postService: PostService,
    private route: ActivatedRoute,
    private linkService: HateoasLinksService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const defaultChannel = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL
    );
    const defaultStatus = this.linkService.getLink(LinkKey.NONE_POST_STATUS);

    this.route.queryParams
      .pipe(
        // 1. Map the full queryParams to an object with only feed-related params:
        map((params) => {
          return {
            page: +params['page'] || 1,
            size: +params['size'] || 10,
            inChannel: params['inChannel'] || defaultChannel,
            withStatus: params['withStatus'] || defaultStatus,
            // Ensure tags is always an array
            withTag: Array.isArray(params['withTag'])
              ? params['withTag']
              : params['withTag']
              ? [params['withTag']]
              : [],
          };
        }),

        // 2. Use distinctUntilChanged to ignore changes that do NOT affect feed parameters:
        distinctUntilChanged((prev, curr) => {
          return (
            prev.page === curr.page &&
            prev.size === curr.size &&
            prev.inChannel === curr.inChannel &&
            prev.withStatus === curr.withStatus &&
            JSON.stringify(prev.withTag) === JSON.stringify(curr.withTag)
          );
        }),

        // 3. Load posts only when the relevant feed parameters have actually changed
        switchMap((feedParams) => {
          return this.loadPosts(feedParams);
        })
      )
      .subscribe({
        next: () => {
          // feedParams changed => new posts loaded
        },
        error: (err) => console.error(err),
      });
  }

  loadPosts(feedParams: {
    page: number;
    size: number;
    inChannel: string;
    withStatus: string;
    withTag: string[];
  }): Observable<void> {
    this.currentPage = feedParams.page;
    this.pageSize = feedParams.size;
    this.channel = feedParams.inChannel;
    this.postStatus = feedParams.withStatus;
    this.tags = feedParams.withTag;

    return this.postService.getPosts(feedParams).pipe(
      map((response) => {
        if (response) {
          this.postList = response.posts;
          this.totalPages = response.totalPages;
          this.currentPage = response.currentPage;
        } else {
          this.postList = [];
          this.totalPages = 0;
        }
        this.loading = false;
      }),
      catchError((error) => {
        console.error('Error loading posts:', error);
        this.postList = [];
        this.totalPages = 0;
        this.loading = false;
        return of();
      })
    );
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.updateQueryParams();

    const feedParams = {
      page: this.currentPage,
      size: this.pageSize,
      inChannel: this.channel,
      withStatus: this.postStatus,
      withTag: this.tags,
    };
    this.loadPosts(feedParams).subscribe();
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        page: this.currentPage,
        size: this.pageSize,
        inChannel: this.channel,
        withStatus: this.postStatus,
        withTag: this.tags,
      },
      queryParamsHandling: 'merge',
    });
  }
}
