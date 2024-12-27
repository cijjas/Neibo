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
    ServiceProvidersReviewsAndPostsComponent
} from '@features/index';

import { SharedModule } from '@shared/shared.module';
import { CalendarWidgetComponent } from '@features/index';
import { ServiceProvidersRoutingModule } from './service-providers-routing.module';

@NgModule({
    declarations: [
        ServiceProvidersContentComponent,
        ServiceProvidersControlBarComponent,
        ServiceProvidersDetailPageComponent,
        ServiceProvidersEditDialogComponent,
        ServiceProvidersPageComponent,
        ServiceProvidersPostPreviewComponent,
        ServiceProvidersPreviewComponent,
        ServiceProvidersReviewDialogComponent,
        ServiceProvidersReviewsAndPostsComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        CalendarWidgetComponent,
        ServiceProvidersRoutingModule
    ],
    exports: [
        ServiceProvidersContentComponent,
        ServiceProvidersControlBarComponent,
        ServiceProvidersDetailPageComponent,
        ServiceProvidersEditDialogComponent,
        ServiceProvidersPageComponent,
        ServiceProvidersPostPreviewComponent,
        ServiceProvidersPreviewComponent,
        ServiceProvidersReviewDialogComponent,
        ServiceProvidersReviewsAndPostsComponent
    ]
})
export class ServiceProvidersModule { }
