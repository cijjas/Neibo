import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { formatDistanceToNow } from 'date-fns';
import { SafeUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { ImageService, HateoasLinksService, UserSessionService } from '@core/index';
import { LikeService, TagService, Post, Tag } from '@shared/index';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-blog-post',
  templateUrl: './feed-post-preview.component.html',
})
export class FeedPostPreviewComponent implements OnInit, OnDestroy {
  @Input() post!: Post;
  humanReadableDate!: string;
  postImageSafeUrl: SafeUrl | null = null;
  authorImageSafeUrl: SafeUrl | null = null;
  isPostImageFallback: boolean = false;
  private subscriptions: Subscription = new Subscription();
  private timer: any;
  tags: Tag[] = [];
  hasLiked: boolean = false;
  likesUrl: string | undefined;
  SPAInChannel: string;

  constructor(
    private linkStorage: HateoasLinksService,
    private imageService: ImageService,
    private tagService: TagService,
    private likeService: LikeService,
    private userSessionService: UserSessionService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.SPAInChannel = this.route.snapshot.queryParams['SPAInChannel'];

    const tagLink = this.linkStorage.getLink('neighborhood:tags');
    this.likesUrl = this.linkStorage.getLink('neighborhood:likes');
    this.loadLikeStatus();

    this.tagService.getTags(tagLink, { onPost: this.post.self }).subscribe({
      next: (tags) => {
        this.tags = tags || [];
      },
      error: (err) => {
        console.error('Error fetching tags:', err);
        this.tags = [];
      },
    });

    this.updateHumanReadableDate();
    this.timer = setInterval(() => this.updateHumanReadableDate(), 60000); // Update every minute

    const postImageSub = this.imageService.fetchImage(this.post.image).subscribe(({ safeUrl, isFallback }) => {
      this.postImageSafeUrl = safeUrl;
      this.isPostImageFallback = isFallback;
    });
    this.subscriptions.add(postImageSub);

    const authorImageSub = this.imageService.fetchImage(this.post.author.image).subscribe(({ safeUrl }) => {
      this.authorImageSafeUrl = safeUrl;
    });
    this.subscriptions.add(authorImageSub);
  }

  private updateHumanReadableDate(): void {
    if (this.post.createdAt) {
      this.humanReadableDate = formatDistanceToNow(new Date(this.post.createdAt), { addSuffix: true });
    }
  }


  private loadLikeStatus(): void {
    if (this.likesUrl) {
      this.likeService.getLikes(this.likesUrl, { onPost: this.post.self }).subscribe({
        next: (response) => {
          this.hasLiked = response.likes.some((like) => like.post.self === this.post.self);
        },
        error: (err) => {
          console.error('Error fetching like status:', err);
        }
      });
    }
  }

  toggleLike(): void {
    if (this.hasLiked) {
      this.unlikePost();
    } else {
      this.likePost();
    }
  }

  private likePost(): void {
    if (this.likesUrl) {
      var userUrl: string;
      this.userSessionService.getCurrentUser().subscribe(
        (user) => userUrl = user.self
      )
      this.likeService.createLike(
        {
          post: this.post.self,
          user: userUrl
        }
      ).subscribe({
        next: () => {
          this.hasLiked = true;
          this.post.likeCount++;
        },
        error: (err) => {
          console.error('Error liking post:', err);
        }
      });
    }
  }

  private unlikePost(): void {
    if (this.likesUrl) {
      var userUrl: string;
      this.userSessionService.getCurrentUser().subscribe(
        (user) => userUrl = user.self
      )
      this.likeService.deleteLike(this.post.self, userUrl).subscribe({ // Replace 'current-user-id' appropriately
        next: () => {
          this.hasLiked = false;
          this.post.likeCount--;
        },
        error: (err) => {
          console.error('Error unliking post:', err);
        }
      });
    }
  }

  filterByTag(tagName: string): void {
    // Get the current query parameters
    const currentQueryParams = { ...this.route.snapshot.queryParams };

    // Update or replace the SPAWithTags parameter
    currentQueryParams['SPAWithTags'] = tagName;

    // Ensure no additional SPAWithTags values are present
    // Preserve the current channel and page
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: currentQueryParams,
      queryParamsHandling: 'merge',
    });
  }



  ngOnDestroy(): void {
    clearInterval(this.timer);
    this.subscriptions.unsubscribe();
  }
}
