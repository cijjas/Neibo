import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { FeedRoutingModule } from './feed-routing.module';

import { FeedControlBarComponent } from './feed-control-bar/feed-control-bar.component';
import { FeedCreatePostPageComponent } from './feed-create-post-page/feed-create-post-page.component';
import { FeedPageComponent } from './feed-page/feed-page.component';
import { FeedPostContentComponent } from './feed-post-content/feed-post-content.component';
import { FeedPostDetailPageComponent } from './feed-post-detail-page/feed-post-detail-page.component';
import { FeedPostPreviewComponent } from './feed-post-preview/feed-post-preview.component';
import { CalendarWidgetComponent } from '@features/calendar/calendar-widget/calendar-widget.component';
import { CalendarBridgeModule } from '@features/calendar/calendar-bridge.module';

@NgModule({
  declarations: [
    FeedControlBarComponent,
    FeedCreatePostPageComponent,
    FeedPageComponent,
    FeedPostContentComponent,
    FeedPostDetailPageComponent,
    FeedPostPreviewComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    CalendarBridgeModule,
    FeedRoutingModule,
  ],
  exports: [
    FeedControlBarComponent,
    FeedCreatePostPageComponent,
    FeedPageComponent,
    FeedPostContentComponent,
    FeedPostDetailPageComponent,
    FeedPostPreviewComponent,
  ],
})
export class FeedModule {}
