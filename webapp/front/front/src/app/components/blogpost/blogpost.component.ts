import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { formatDistanceToNow } from 'date-fns';
import { Post } from '../../shared/models/index';
import { ImageService } from '../../shared/services/core/image.service';
import { SafeUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';

@Component({
  selector: 'blogpost',
  templateUrl: './blogpost.component.html',
})
export class BlogpostComponent implements OnInit, OnDestroy {
  @Input() post!: Post;
  humanReadableDate!: string;
  postImageSafeUrl: SafeUrl | null = null;
  authorImageSafeUrl: SafeUrl | null = null;
  private subscriptions: Subscription = new Subscription();
  private timer: any;

  constructor(private imageService: ImageService) { }

  ngOnInit(): void {
    this.updateHumanReadableDate();
    this.timer = setInterval(() => this.updateHumanReadableDate(), 60000); // Update every minute

    // Fetch the post image if it exists
    if (this.post.image) {
      const postImageSub = this.imageService.fetchImage(this.post.image).subscribe((safeUrl) => {
        this.postImageSafeUrl = safeUrl;
      });
      this.subscriptions.add(postImageSub);
    }

    // Fetch the author's profile image if it exists
    if (this.post.author.image) {
      const authorImageSub = this.imageService.fetchImage(this.post.author.image).subscribe((safeUrl) => {
        this.authorImageSafeUrl = safeUrl;
      });
      this.subscriptions.add(authorImageSub);
    }
  }

  private updateHumanReadableDate(): void {
    if (this.post.createdAt) {
      this.humanReadableDate = formatDistanceToNow(new Date(this.post.createdAt), { addSuffix: true });
    }
  }

  ngOnDestroy(): void {
    clearInterval(this.timer); // Clear the timer when the component is destroyed
    this.subscriptions.unsubscribe(); // Unsubscribe from all subscriptions

    // Revoke object URLs to prevent memory leaks
    if (this.postImageSafeUrl) {
      URL.revokeObjectURL((this.postImageSafeUrl as any).changingThisBreaksApplicationSecurity);
    }
    if (this.authorImageSafeUrl) {
      URL.revokeObjectURL((this.authorImageSafeUrl as any).changingThisBreaksApplicationSecurity);
    }
  }
}
