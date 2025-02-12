import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { formatDistanceToNow } from 'date-fns';

import { Post, Comment, Tag, LinkKey } from '@shared/index';
import { CommentService, LikeService, TagService } from '@shared/index';

import { SafeUrl } from '@angular/platform-browser';
import { Subscription, take } from 'rxjs';
import {
  HateoasLinksService,
  UserSessionService,
  ImageService,
} from '@core/index';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { environment } from 'environments/environment';
import { enUS, es } from 'date-fns/locale';

@Component({
  selector: 'app-feed-post-content',
  templateUrl: './feed-post-content.component.html',
})
export class FeedPostContentComponent implements OnInit, OnDestroy {
  @Input() post!: Post;
  isFocused: boolean = false;

  humanReadableDate!: string;
  postImageSafeUrl: SafeUrl | null = null;
  authorImageSafeUrl: SafeUrl | null = null;
  comments: Comment[] = [];
  totalPages: number = 1;
  currentPage: number = 1;
  pageSize: number = 10;
  isPostImageFallback: boolean = false;
  commentForm: FormGroup;
  private subscriptions: Subscription = new Subscription();
  private timer: any;

  tags: Tag[] = [];
  isLiked: boolean = false; // Like status
  likesUrl: string | undefined;
  environment = environment;

  loadingComments = true;

  constructor(
    private fb: FormBuilder,
    private imageService: ImageService,
    private commentService: CommentService,
    private linkStorage: HateoasLinksService,
    private likeService: LikeService,
    private tagService: TagService,
    private userSessionService: UserSessionService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.commentForm = this.fb.group({
      comment: ['', [Validators.required]],
    });

    const tagLink = this.linkStorage.getLink(LinkKey.NEIGHBORHOOD_TAGS);
    this.likesUrl = this.linkStorage.getLink(LinkKey.NEIGHBORHOOD_LIKES);

    this.loadLikeStatus();
    this.fetchTags(tagLink);

    // Monitor query parameters
    this.route.queryParams.subscribe(params => {
      this.currentPage = +params['page'] || 1;
      this.pageSize = +params['size'] || 10;
      this.getComments(this.currentPage, this.pageSize);
    });

    this.updateHumanReadableDate();
    this.timer = setInterval(() => this.updateHumanReadableDate(), 60000); // Update every minute

    const postImageSub = this.imageService
      .fetchImage(this.post.image)
      .subscribe(({ safeUrl, isFallback }) => {
        this.postImageSafeUrl = safeUrl;
        this.isPostImageFallback = isFallback;
      });
    this.subscriptions.add(postImageSub);

    const authorImageSub = this.imageService
      .fetchImage(this.post.author.image)
      .subscribe(({ safeUrl }) => {
        this.authorImageSafeUrl = safeUrl;
      });
    this.subscriptions.add(authorImageSub);
  }

  /**
   * Likes
   */
  private loadLikeStatus(): void {
    if (!this.likesUrl) return;
    const user = this.userSessionService.getCurrentUserValue();
    if (!user) {
      return;
    }
    this.likeService
      .getLikes(this.likesUrl, {
        onPost: this.post.self,
        likedBy: user.self,
      })
      .subscribe({
        next: response => {
          // If the array is non-empty, the current user liked this post
          this.isLiked = response.likes.length > 0;
        },
        error: err => {
          console.error('Error fetching like status:', err);
        },
      });
  }

  toggleLike(): void {
    if (this.isLiked) {
      this.unlikePost();
    } else {
      this.likePost();
    }
  }

  private likePost(): void {
    const user = this.userSessionService.getCurrentUserValue();
    if (!user) {
      return;
    }
    this.likeService
      .createLike({ post: this.post.self, user: user.self })
      .subscribe({
        next: () => {
          this.isLiked = true;
          this.post.likeCount++;
        },
        error: err => {
          console.error('Error liking post:', err);
        },
      });
  }

  private unlikePost(): void {
    const user = this.userSessionService.getCurrentUserValue();
    if (!user) {
      return;
    }
    this.likeService.deleteLike(this.post.self, user.self).subscribe({
      next: () => {
        this.isLiked = false;
        this.post.likeCount--;
      },
      error: err => {
        console.error('Error unliking post:', err);
      },
    });
  }

  private fetchTags(tagLink: string | undefined): void {
    if (tagLink) {
      this.tagService.getTags(tagLink, { onPost: this.post.self }).subscribe({
        next: ({ tags }: { tags: Tag[] }) => {
          this.tags = tags || [];
        },
        error: err => {
          console.error('Error fetching tags:', err);
          this.tags = [];
        },
      });
    }
  }

  /**
   * Comments
   */
  onSubmit() {
    if (this.commentForm.invalid) {
      console.error('Comment form is invalid');
      return;
    }
    const user = this.userSessionService.getCurrentUserValue();
    if (!user) {
      return;
    }

    this.commentService
      .createComment(
        this.post.comments,
        this.commentForm.get('comment')?.value,
        user.self,
      )
      .subscribe({
        next: createdComment => {
          // Reset the form and explicitly set comment to an empty string.
          this.commentForm.reset({ comment: '' });
          this.isFocused = false;
          this.getComments(this.currentPage, this.pageSize);
        },
        error: error => {
          console.error('Error creating comment:', error);
        },
      });
  }

  onCancel(): void {
    // Reset with default value to avoid null
    this.commentForm.reset({ comment: '' });
    this.isFocused = false;
  }

  getComments(page: number, size: number): void {
    this.loadingComments = true;
    if (this.post?.comments) {
      this.commentService
        .getComments(this.post.comments, { page, size })
        .subscribe({
          next: response => {
            this.comments = response.comments || [];
            this.totalPages = response.totalPages || 1;
            this.currentPage = response.currentPage || 1;
            this.loadingComments = false;
          },
          error: err => {
            console.error('Error fetching comments:', err);
            this.comments = [];
            this.loadingComments = false;
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
      const locale = localStorage.getItem('language') == 'es' ? es : enUS;
      this.humanReadableDate = formatDistanceToNow(
        new Date(this.post.createdAt),
        { addSuffix: true, locale: locale },
      );
    }
  }

  ngOnDestroy(): void {
    clearInterval(this.timer); // Clear the timer when the component is destroyed
    this.subscriptions.unsubscribe(); // Unsubscribe from all subscriptions
  }

  filterByTag(tagName: string): void {
    // Get the current query parameters
    const currentQueryParams = { ...this.route.snapshot.queryParams };

    currentQueryParams['withTag'] = tagName;

    // Navigate to /posts with the updated query parameters
    this.router.navigate(['/posts'], {
      queryParams: currentQueryParams,
      queryParamsHandling: 'merge',
    });
  }
}
