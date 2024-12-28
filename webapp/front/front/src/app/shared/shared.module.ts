import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router'; // For directives like routerLink
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Import shared components and pipes
import {
    NavbarComponent,
    SuccessToastComponent,
    SidebarComponent,
    WaveFooterComponent,
    BackgroundCloudsComponent,
    LandingPageNavbarComponent,
    BackgroundDrawingComponent,
    PaginatorComponent,
    RightColumnComponent,
    TagsFilterWidgetComponent
} from "@shared/index";

import { TimeAgoPipe, AddHoursPipe } from "@shared/index";
import { CalendarWidgetComponent, UserProfileWidgetComponent } from '@features/index';

@NgModule({
    declarations: [
        NavbarComponent,
        PaginatorComponent,
        SuccessToastComponent,
        SidebarComponent,
        WaveFooterComponent,
        BackgroundCloudsComponent,
        LandingPageNavbarComponent,
        RightColumnComponent,
        BackgroundDrawingComponent,
        TagsFilterWidgetComponent,
    ],
    imports: [
        CommonModule,
        RouterModule, // Add this for routerLink and related directives
        FormsModule,
        ReactiveFormsModule,
        TimeAgoPipe,
        AddHoursPipe,
        CalendarWidgetComponent,
        UserProfileWidgetComponent
    ],
    exports: [
        CommonModule,
        RouterModule, // Export this to make router directives available in other modules
        FormsModule,
        ReactiveFormsModule,

        NavbarComponent,
        PaginatorComponent,
        SuccessToastComponent,
        SidebarComponent,
        WaveFooterComponent,
        BackgroundCloudsComponent,
        LandingPageNavbarComponent,
        RightColumnComponent,
        BackgroundDrawingComponent,
        TagsFilterWidgetComponent,

        TimeAgoPipe,
        AddHoursPipe,
    ]
})
export class SharedModule { }
