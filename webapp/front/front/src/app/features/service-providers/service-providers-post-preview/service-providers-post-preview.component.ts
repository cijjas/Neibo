import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { formatDistanceToNow } from 'date-fns';
import { Post } from '@shared/index';

@Component({
  selector: 'app-service-providers-post-preview',
  templateUrl: './service-providers-post-preview.component.html',
})
export class ServiceProvidersPostPreviewComponent implements OnInit, OnDestroy {
  @Input() post!: Post;
  humanReadableDate!: string;
  private timer: any;

  ngOnInit(): void {
    // Initialize the human-readable date
    this.updateHumanReadableDate();

    // Set a timer to update the date every minute
    this.timer = setInterval(() => this.updateHumanReadableDate(), 60000); // 1-minute interval
  }

  private updateHumanReadableDate(): void {
    if (this.post.createdAt) {
      this.humanReadableDate = formatDistanceToNow(new Date(this.post.createdAt), { addSuffix: true });
    }
  }

  ngOnDestroy(): void {
    // Clear the timer to avoid memory leaks
    clearInterval(this.timer);
  }
}
