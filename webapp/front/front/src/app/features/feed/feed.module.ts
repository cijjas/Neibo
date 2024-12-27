import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
    FeedControlBarComponent,
    FeedCreatePostPageComponent,
    FeedPageComponent,
    FeedPostContentComponent,
    FeedPostDetailPageComponent,
    FeedPostPreviewComponent
} from '@features/index';

import { SharedModule } from '@shared/shared.module';
import { CalendarWidgetComponent } from '@features/index';
import { FeedRoutingModule } from './feed-routing.module';

@NgModule({
    declarations: [
        FeedControlBarComponent,
        FeedCreatePostPageComponent,
        FeedPageComponent,
        FeedPostContentComponent,
        FeedPostDetailPageComponent,
        FeedPostPreviewComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        CalendarWidgetComponent,
        FeedRoutingModule
    ],
    exports: [
        FeedControlBarComponent,
        FeedCreatePostPageComponent,
        FeedPageComponent,
        FeedPostContentComponent,
        FeedPostDetailPageComponent,
        FeedPostPreviewComponent
    ]
})
export class FeedModule { }
