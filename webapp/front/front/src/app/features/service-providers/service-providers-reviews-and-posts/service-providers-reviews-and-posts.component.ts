import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Review, Post, Worker, WorkerService, LinkKey } from '@shared/index';
import { ReviewService, PostService } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Component({
  selector: 'app-service-providers-reviews-and-posts',
  templateUrl: './service-providers-reviews-and-posts.component.html',
})
export class ServiceProvidersReviewsAndPostsComponent
  implements OnInit, OnDestroy
{
  private workerSubject = new BehaviorSubject<Worker | null>(null);
  private subscriptions = new Subscription();

  @Output() openReviewDialog = new EventEmitter<void>();

  reviews: Review[] = [];
  posts: Post[] = [];

  reviewTotalPages = 1;
  reviewCurrentPage = 1;
  reviewPageSize = 10;

  postTotalPages = 1;
  postCurrentPage = 1;
  postPageSize = 10;

  isTheWorker = false;
  isWorker = false;

  worker: Worker = null;

  // Track which tab is selected. Possible values: 'reviews' or 'posts'.
  selectedTab: 'reviews' | 'posts' = 'reviews';

  constructor(
    private reviewService: ReviewService,
    private linkService: HateoasLinksService,
    private postService: PostService,
    private route: ActivatedRoute,
    private router: Router,
    private workerService: WorkerService
  ) {}

  ngOnInit(): void {
    const workerId = this.route.snapshot.paramMap.get('id');
    if (workerId) {
      this.loadWorker(workerId).then(() => {
        // Execute only after worker is loaded
        this.isTheWorker =
          this.linkService.getLink(LinkKey.USER_WORKER) === this.worker.self;
        this.isWorker =
          this.linkService.getLink(LinkKey.USER_USER_ROLE) ===
          this.linkService.getLink(LinkKey.WORKER_USER_ROLE);

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

            // Reload data after query params change
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
      });
    }
  }

  loadWorker(id: string): Promise<void> {
    return new Promise((resolve, reject) => {
      this.workerService.getWorker(id).subscribe({
        next: (worker) => {
          this.worker = worker;
          this.workerSubject.next(worker); // Update workerSubject
          resolve();
        },
        error: (err) => {
          console.error('Error loading worker:', err);
          reject(err);
        },
      });
    });
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
      const queryParams = {
        page: this.reviewCurrentPage,
        size: this.reviewPageSize,
      };
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
      const queryParams = {
        page: this.postCurrentPage,
        size: this.postPageSize,
      };
      this.postService.getWorkerPosts(queryParams).subscribe({
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
  goToServicePostCreation() {
    this.router.navigate(['/services', 'posts', 'new'], {
      queryParams: {
        inChannel: 'Give me the CHanneeeeel',
      },
    });
  }
}
