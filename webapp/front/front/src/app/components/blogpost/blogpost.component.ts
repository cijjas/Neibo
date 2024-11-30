import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { formatDistanceToNow } from 'date-fns';
import { Post } from '../../shared/models/index';
import { ImageService } from '../../shared/services/core/image.service';
import { SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'blogpost',
  templateUrl: './blogpost.component.html',
})
export class BlogpostComponent implements OnInit, OnDestroy {
  @Input() post!: Post;
  humanReadableDate!: string;
  postImageSafeUrl: SafeUrl | null = null; // Safe URL for the post image
  private timer: any;

  constructor(private imageService: ImageService) { }

  ngOnInit(): void {
    this.updateHumanReadableDate();
    this.timer = setInterval(() => this.updateHumanReadableDate(), 60000); // Update every minute

    if (this.post.image) {
      this.imageService.fetchImage(this.post.image).subscribe((safeUrl) => {
        this.postImageSafeUrl = safeUrl;
      });
    }
  }

  private updateHumanReadableDate(): void {
    if (this.post.createdAt) {
      this.humanReadableDate = formatDistanceToNow(new Date(this.post.createdAt), { addSuffix: true });
    }
  }

  ngOnDestroy(): void {
    clearInterval(this.timer); // Clear the timer when the component is destroyed
  }
}
