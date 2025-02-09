import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { formatDistanceToNow } from 'date-fns';
import { Post } from '@shared/index';
import { environment } from '../../../../environments/environment';
import { enUS, es } from 'date-fns/locale';

@Component({
  selector: 'app-service-providers-post-preview',
  templateUrl: './service-providers-post-preview.component.html',
})
export class ServiceProvidersPostPreviewComponent implements OnInit, OnDestroy {
  environment = environment;
  @Input() post!: Post;
  humanReadableDate!: string;
  private timer: any;

  ngOnInit(): void {
    this.updateHumanReadableDate();

    this.timer = setInterval(() => this.updateHumanReadableDate(), 60000);
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
    clearInterval(this.timer);
  }
}
