import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ServiceProvidersContentComponent,
  ServiceProvidersControlBarComponent,
  ServiceProvidersDetailPageComponent,
  ServiceProvidersEditDialogComponent,
  ServiceProvidersPageComponent,
  ServiceProvidersPostPreviewComponent,
  ServiceProvidersPreviewComponent,
  ServiceProvidersReviewDialogComponent,
  ServiceProvidersReviewsAndPostsComponent,
  ServiceProvidersJoinNeighborhoodsComponent,
} from '@features/index';

import { SharedModule } from '@shared/shared.module';
import { CalendarWidgetComponent } from '@features/index';
import { ServiceProvidersRoutingModule } from './service-providers-routing.module';
import { ServiceProvidersLayoutComponent } from './service-providers-layout/service-providers-layout.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ServiceProvidersCreatePostComponent } from './service-providers-create-post/service-providers-create-post.component';

@NgModule({
  declarations: [
    ServiceProvidersLayoutComponent,
    ServiceProvidersContentComponent,
    ServiceProvidersControlBarComponent,
    ServiceProvidersDetailPageComponent,
    ServiceProvidersEditDialogComponent,
    ServiceProvidersPageComponent,
    ServiceProvidersPostPreviewComponent,
    ServiceProvidersPreviewComponent,
    ServiceProvidersReviewDialogComponent,
    ServiceProvidersReviewsAndPostsComponent,
    ServiceProvidersJoinNeighborhoodsComponent,
    ServiceProvidersCreatePostComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    CalendarWidgetComponent,
    ServiceProvidersRoutingModule,
  ],
  exports: [],
})
export class ServiceProvidersModule {}
