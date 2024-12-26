import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Review, Post, Worker } from '../../shared/models';
import { ReviewService, PostService } from '../../shared/services/index.service';

@Component({
  selector: 'app-tabbed-box',
  templateUrl: './tabbed-box.component.html',
})
export class TabbedBoxComponent implements OnInit, OnDestroy {
  private workerSubject = new BehaviorSubject<Worker | null>(null);
  private subscriptions = new Subscription();

  @Input()
  set worker(value: Worker | null) {
    this.workerSubject.next(value);
  }
  @Input() loggedUser: any = null;
  @Output() openReviewDialog = new EventEmitter<void>();

  reviews: Review[] = [];
  posts: Post[] = [];

  reviewTotalPages = 1;
  reviewCurrentPage = 1;
  reviewPageSize = 10;

  postTotalPages = 1;
  postCurrentPage = 1;
  postPageSize = 10;

  // Track which tab is selected. Possible values: 'reviews' or 'posts'.
  selectedTab: 'reviews' | 'posts' = 'reviews';

  constructor(
    private reviewService: ReviewService,
    private postService: PostService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Subscribe to query parameters for pagination *and* for tab selection
    this.subscriptions.add(
      this.route.queryParams.subscribe((params) => {
        // Pagination params
        this.reviewCurrentPage = +params['reviewPage'] || 1;
        this.reviewPageSize = +params['reviewSize'] || 10;
        this.postCurrentPage = +params['postPage'] || 1;
        this.postPageSize = +params['postSize'] || 10;

        // Tab param (default to "reviews" if not set)
        this.selectedTab = params['tab'] === 'posts' ? 'posts' : 'reviews';

        // Whenever query params change, reload the data
        this.loadReviews();
        this.loadPosts();
      })
    );

    // Reactively load data when the worker changes
    this.subscriptions.add(
      this.workerSubject
        .pipe(
          switchMap((worker) => {
            if (worker) {
              this.loadReviews();
              this.loadPosts();
            }
            return [];
          })
        )
        .subscribe()
    );
  }

  /**
   * Called when the user switches tabs (e.g., from Reviews to Posts).
   */
  onTabChange(tab: 'reviews' | 'posts'): void {
    this.selectedTab = tab;
    // Update just the `tab` param in the URL, merging with existing params
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { tab },
      queryParamsHandling: 'merge',
    });
  }

  loadReviews(): void {
    const worker = this.workerSubject.getValue();
    if (worker?.reviews) {
      const queryParams = { page: this.reviewCurrentPage, size: this.reviewPageSize };
      this.reviewService.getReviews(worker.reviews, queryParams).subscribe({
        next: (response) => {
          this.reviews = response.reviews;
          this.reviewTotalPages = response.totalPages;
        },
        error: (err) => console.error('Error loading reviews:', err),
      });
    }
  }

  loadPosts(): void {
    const worker = this.workerSubject.getValue();
    if (worker?.posts) {
      const queryParams = { page: this.postCurrentPage, size: this.postPageSize };
      this.postService.getPosts(worker.posts, queryParams).subscribe({
        next: (response) => {
          this.posts = response.posts;
          this.postTotalPages = response.totalPages;
        },
        error: (err) => console.error('Error loading posts:', err),
      });
    }
  }

  onReviewPageChange(page: number): void {
    this.reviewCurrentPage = page;
    this.updateQueryParams({ reviewPage: page });
    this.loadReviews();
  }

  onPostPageChange(page: number): void {
    this.postCurrentPage = page;
    this.updateQueryParams({ postPage: page });
    this.loadPosts();
  }

  private updateQueryParams(params: any): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: params,
      queryParamsHandling: 'merge',
    });
  }

  renderStars(rating: number): string {
    const fullStars = Math.floor(rating);
    const halfStar = rating - fullStars;
    const emptyStars = 5 - fullStars - (halfStar > 0 ? 1 : 0);

    let starsHtml = '';
    for (let i = 0; i < fullStars; i++) {
      starsHtml += '<i class="fa-solid fa-star cool-star active"></i>';
    }
    if (halfStar > 0) {
      starsHtml += '<i class="fa-solid fa-star-half-stroke"></i>';
    }
    for (let i = 0; i < emptyStars; i++) {
      starsHtml += '<i class="fa-regular fa-star cool-star"></i>';
    }
    return starsHtml;
  }

  public reloadReviews(): void {
    this.loadReviews();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
