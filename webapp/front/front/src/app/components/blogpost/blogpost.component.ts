import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { formatDistanceToNow } from 'date-fns';
import { Post, Tag } from '../../shared/models/index';
import { ImageService } from '../../shared/services/core/image.service';
import { SafeUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { HateoasLinksService, TagService } from '../../shared/services/index.service';

@Component({
  selector: 'blogpost',
  templateUrl: './blogpost.component.html',
})
export class BlogpostComponent implements OnInit, OnDestroy {
  @Input() post!: Post;
  humanReadableDate!: string;
  postImageSafeUrl: SafeUrl | null = null;
  authorImageSafeUrl: SafeUrl | null = null;
  isPostImageFallback: boolean = false;
  private subscriptions: Subscription = new Subscription();
  private timer: any;
  tags: Tag[] = [];

  constructor(
    private linkStorage: HateoasLinksService,
    private imageService: ImageService,
    private tagService: TagService
  ) { }

  ngOnInit(): void {
    const tagLink = this.linkStorage.getLink('neighborhood:tags');

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

  ngOnDestroy(): void {
    clearInterval(this.timer);
    this.subscriptions.unsubscribe();
  }
}
