import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { ServiceProvidersRoutingModule } from './service-providers-routing.module';

import { CalendarWidgetComponent } from '../calendar/calendar-widget/calendar-widget.component';

import { ServiceProvidersContentComponent } from './service-providers-content/service-providers-content.component';
import { ServiceProvidersDetailPageComponent } from './service-providers-detail-page/service-providers-detail-page.component';
import { ServiceProvidersEditDialogComponent } from './service-providers-edit-dialog/service-providers-edit-dialog.component';
import { ServiceProvidersPageComponent } from './service-providers-page/service-providers-page.component';
import { ServiceProvidersPostPreviewComponent } from './service-providers-post-preview/service-providers-post-preview.component';
import { ServiceProvidersPreviewComponent } from './service-providers-preview/service-providers-preview.component';
import { ServiceProvidersReviewDialogComponent } from './service-providers-review-dialog/service-providers-review-dialog.component';
import { ServiceProvidersReviewsAndPostsComponent } from './service-providers-reviews-and-posts/service-providers-reviews-and-posts.component';
import { ServiceProvidersJoinNeighborhoodsPageComponent } from '@features/index';
import { ServiceProvidersLayoutComponent } from './service-providers-layout/service-providers-layout.component';
import { ServiceProvidersCreatePostComponent } from './service-providers-create-post/service-providers-create-post.component';
import { CalendarBridgeModule } from '@features/calendar/calendar-bridge.module';

@NgModule({
  declarations: [
    ServiceProvidersLayoutComponent,
    ServiceProvidersContentComponent,
    ServiceProvidersDetailPageComponent,
    ServiceProvidersEditDialogComponent,
    ServiceProvidersPageComponent,
    ServiceProvidersPostPreviewComponent,
    ServiceProvidersPreviewComponent,
    ServiceProvidersReviewDialogComponent,
    ServiceProvidersReviewsAndPostsComponent,
    ServiceProvidersJoinNeighborhoodsPageComponent,
    ServiceProvidersCreatePostComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    ServiceProvidersRoutingModule,
    CalendarBridgeModule,
  ],
  exports: [],
})
export class ServiceProvidersModule {}
