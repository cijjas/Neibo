import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { formatDistanceToNow } from 'date-fns';
import { Post, Comment } from '../../shared/models';
import { ImageService } from '../../shared/services/core/image.service';
import { SafeUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { CommentService, HateoasLinksService } from '../../shared/services/index.service';

@Component({
  selector: 'app-post-card',
  templateUrl: './post-card.component.html',
})
export class PostCardComponent implements OnInit, OnDestroy {
  @Input() post!: Post;

  humanReadableDate!: string;
  postImageSafeUrl: SafeUrl | null = null;
  authorImageSafeUrl: SafeUrl | null = null;
  comments: Comment[] = [];
  totalPages: number = 1;
  currentPage: number = 1;
  pageSize: number = 10;
  isPostImageFallback: boolean = false;
  private subscriptions: Subscription = new Subscription();
  private timer: any;

  constructor(
    private imageService: ImageService,
    private commentService: CommentService,
    private linkStorage: HateoasLinksService,
    private router: Router,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    // Monitor query parameters
    this.route.queryParams.subscribe((params) => {
      this.currentPage = +params['page'] || 1; // Default to page 1
      this.pageSize = +params['size'] || 10; // Default to size 10

      // Fetch comments based on query parameters
      this.getComments(this.currentPage, this.pageSize);
    });

    // Set human-readable date
    this.updateHumanReadableDate();
    this.timer = setInterval(() => this.updateHumanReadableDate(), 60000); // Update every minute

    // Fetch post image
    const postImageSub = this.imageService.fetchImage(this.post.image).subscribe(({ safeUrl, isFallback }) => {
      this.postImageSafeUrl = safeUrl;
      this.isPostImageFallback = isFallback;
    });
    this.subscriptions.add(postImageSub);

    // Fetch author's profile image
    const authorImageSub = this.imageService.fetchImage(this.post.author.image).subscribe(({ safeUrl }) => {
      this.authorImageSafeUrl = safeUrl;
    });
    this.subscriptions.add(authorImageSub);
  }

  getComments(page: number, size: number): void {
    if (this.post?.comments) {
      this.commentService.getComments(this.post.comments, { page, size }).subscribe({
        next: (response) => {
          this.comments = response.comments || [];
          this.totalPages = response.totalPages || 1;
          this.currentPage = response.currentPage || 1;
        },
        error: (err) => {
          console.error('Error fetching comments:', err);
          this.comments = [];
        },
      });
    }
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.updateQueryParams();
    this.getComments(this.currentPage, this.pageSize);
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.currentPage, size: this.pageSize },
      queryParamsHandling: 'merge', // Merge with other existing query params
    });
  }

  private updateHumanReadableDate(): void {
    if (this.post.createdAt) {
      this.humanReadableDate = formatDistanceToNow(new Date(this.post.createdAt), { addSuffix: true });
    }
  }

  ngOnDestroy(): void {
    clearInterval(this.timer); // Clear the timer when the component is destroyed
    this.subscriptions.unsubscribe(); // Unsubscribe from all subscriptions
  }
}