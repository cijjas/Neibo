import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { CalendarRoutingModule } from './calendar-routing.module';

import { CalendarBoxComponent } from './calendar-box/calendar-box.component';
import { CalendarEventsColumnComponent } from './calendar-events-column/calendar-events-column.component';
import { CalendarPageComponent } from './calendar-page/calendar-page.component';
import { CalendarEventPageComponent } from './calendar-event-page/calendar-event-page.component';
import { CalendarWidgetComponent } from './calendar-widget/calendar-widget.component';
import { CalendarBridgeModule } from './calendar-bridge.module';
@NgModule({
  declarations: [
    CalendarBoxComponent,
    CalendarEventPageComponent,
    CalendarEventsColumnComponent,
    CalendarPageComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    CalendarRoutingModule,
    CalendarBridgeModule,
  ],
  exports: [
    CalendarBoxComponent,
    CalendarEventPageComponent,
    CalendarEventsColumnComponent,
    CalendarPageComponent,
  ],
})
export class CalendarModule {}
