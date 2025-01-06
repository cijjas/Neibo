import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of, switchMap } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

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
  ) { }

  ngOnInit(): void {
    const defaultChannel = this.linkService.getLink(LinkKey.NEIGHBORHOOD_FEED_CHANNEL);
    const defaultStatus = this.linkService.getLink(LinkKey.NONE_POST_STATUS);

    this.route.queryParams
      .pipe(
        switchMap((params) => {
          this.currentPage = +params['page'] || 1;
          this.pageSize = +params['size'] || 10;
          this.channel = params['SPAInChannel'] || defaultChannel;
          this.postStatus = params['SPAWithStatus'] || defaultStatus;

          // ---------------------------
          // Handle multiple withTag params
          // Angular automatically parses repeated param keys
          // as an array. If it's a single string, convert it to array manually.
          // If no tags are present, this.tags should become [].
          // ---------------------------
          const tagsParam = params['withTag'];
          this.tags = Array.isArray(tagsParam)
            ? tagsParam
            : tagsParam
              ? [tagsParam]
              : [];

          // Provide default params if missing
          const missingParams: any = {};
          if (!params['SPAInChannel']) {
            missingParams['SPAInChannel'] = defaultChannel;
          }
          if (!params['SPAWithStatus']) {
            missingParams['SPAWithStatus'] = defaultStatus;
          }

          // Navigate to default parameters if any are missing
          if (Object.keys(missingParams).length > 0) {
            this.router.navigate([], {
              relativeTo: this.route,
              queryParams: { ...missingParams },
              queryParamsHandling: 'merge',
            });
          }

          return this.loadPosts();
        })
      )
      .subscribe();
  }



  loadPosts(): Observable<void> {
    // This array can be ["tag1", "tag2", ...]
    const queryParams = {
      page: this.currentPage,
      size: this.pageSize,
      inChannel: this.channel,
      withStatus: this.postStatus,
      withTag: this.tags,
    };

    return this.postService.getPosts(queryParams).pipe(
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
    this.loadPosts().subscribe();
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        page: this.currentPage,
        size: this.pageSize,
        // Add the tags to keep them in the URL
        withTag: this.tags,
      },
      queryParamsHandling: 'merge',
    });
  }



}
