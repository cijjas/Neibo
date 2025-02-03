import { NgModule } from '@angular/core';
import { CalendarWidgetComponent } from '@features/calendar/calendar-widget/calendar-widget.component';

@NgModule({
  imports: [CalendarWidgetComponent], // ✅ Import standalone component
  exports: [CalendarWidgetComponent], // ✅ Export it for reuse
})
export class CalendarBridgeModule {}
