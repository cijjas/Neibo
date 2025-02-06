import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { InformationRoutingModule } from '@features/index';

import { CalendarWidgetComponent } from '@features/index';
import { InformationPageComponent } from '@features/index';
import { CalendarBridgeModule } from '@features/calendar/calendar-bridge.module';

@NgModule({
  declarations: [InformationPageComponent],
  imports: [
    CommonModule,
    SharedModule,
    CalendarBridgeModule,
    InformationRoutingModule,
  ],
  exports: [InformationPageComponent],
})
export class InformationModule {}
