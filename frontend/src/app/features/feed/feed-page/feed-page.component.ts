import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of, switchMap } from 'rxjs';
import { catchError, distinctUntilChanged, map, tap } from 'rxjs/operators';
import { PostService, Post, LinkKey } from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
import { AppTitleKeys } from '@shared/constants/app-titles';

@Component({
  selector: 'app-feed-page',
  templateUrl: './feed-page.component.html',
})
export class FeedPageComponent implements OnInit {
  public postList: Post[] = [];
  public loading: boolean = true;
  public placeholderArray = new Array(10); // Placeholder count

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
    private router: Router,
    private titleService: Title,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.route.queryParams
      .pipe(
        map(params => ({
          page: +params['page'] || 1,
          size: +params['size'] || 10,
          inChannel:
            params['inChannel'] ||
            this.linkService.getLink(
              LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL,
            ),
          withStatus:
            params['withStatus'] ||
            this.linkService.getLink(LinkKey.NONE_POST_STATUS),
          withTag: Array.isArray(params['withTag'])
            ? params['withTag']
            : params['withTag']
            ? [params['withTag']]
            : [],
        })),
        distinctUntilChanged(
          (prev, curr) =>
            prev.page === curr.page &&
            prev.size === curr.size &&
            prev.inChannel === curr.inChannel &&
            prev.withStatus === curr.withStatus &&
            JSON.stringify(prev.withTag) === JSON.stringify(curr.withTag),
        ),
        tap(feedParams => {
          this.channel = feedParams.inChannel;
          const channelTitleKey = this.getFriendlyChannelName(
            feedParams.inChannel,
          );
          const title = this.translate.instant(channelTitleKey);
          this.titleService.setTitle(title);
        }),
        switchMap(feedParams => this.loadPosts(feedParams)),
      )
      .subscribe();
  }

  private getFriendlyChannelName(channelLink: string): string {
    const announcementsChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL,
    );
    const feedChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_FEED_CHANNEL,
    );
    const complaintsChannelUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_COMPLAINTS_CHANNEL,
    );

    switch (channelLink) {
      case announcementsChannelUrl:
        return AppTitleKeys.ANNOUNCEMENTS_PAGE;
      case feedChannelUrl:
        return AppTitleKeys.FEED_PAGE;
      case complaintsChannelUrl:
        return AppTitleKeys.COMPLAINTS_PAGE;
      default:
        return AppTitleKeys.FEED_PAGE;
    }
  }

  loadPosts(feedParams: {
    page: number;
    size: number;
    inChannel: string;
    withStatus: string;
    withTag: string[];
  }): Observable<void> {
    this.loading = true;

    return this.postService.getPosts(feedParams).pipe(
      map(response => {
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
      catchError(error => {
        console.error('Error loading posts:', error);
        this.postList = [];
        this.totalPages = 0;
        this.loading = false;
        return of();
      }),
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
