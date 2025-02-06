import { NgModule } from '@angular/core';
import { CalendarWidgetComponent } from '@features/calendar/calendar-widget/calendar-widget.component';

@NgModule({
  imports: [CalendarWidgetComponent], 
  exports: [CalendarWidgetComponent], 
})
export class CalendarBridgeModule {}
