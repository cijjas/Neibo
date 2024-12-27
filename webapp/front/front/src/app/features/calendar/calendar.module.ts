import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
    CalendarWidgetComponent,
    CalendarBoxComponent,
    CalendarEventPageComponent,
    CalendarEventsColumnComponent,
    CalendarPageComponent
} from '@features/index';

import { SharedModule } from '@shared/shared.module'; // Optional, if CalendarWidget needs shared components
import { CalendarRoutingModule } from './calendar-routing.module';

@NgModule({
    declarations: [
        CalendarBoxComponent,
        CalendarEventPageComponent,
        CalendarEventsColumnComponent,
        CalendarPageComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        CalendarWidgetComponent,
        CalendarRoutingModule
    ],
    exports: [
        CalendarWidgetComponent,
        CalendarBoxComponent,
        CalendarEventPageComponent,
        CalendarEventsColumnComponent,
        CalendarPageComponent
    ]
})
export class CalendarModule { }
