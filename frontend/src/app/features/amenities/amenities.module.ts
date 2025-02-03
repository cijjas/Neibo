import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { AmenitiesRoutingModule } from './amenities-routing.module';

import { AmenitiesChooseTimePageComponent } from './amenities-choose-time-page/amenities-choose-time-page.component';
import { AmenitiesReservationsListComponent } from './amenities-reservations-list/amenities-reservations-list.component';
import { AmenitiesReservationsPageComponent } from './amenities-reservations-page/amenities-reservations-page.component';

import { CalendarWidgetComponent } from '../calendar/calendar-widget/calendar-widget.component';
import { CalendarBridgeModule } from '@features/calendar/calendar-bridge.module';

@NgModule({
  declarations: [
    AmenitiesChooseTimePageComponent,
    AmenitiesReservationsListComponent,
    AmenitiesReservationsPageComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    AmenitiesRoutingModule,
    CalendarBridgeModule,
  ],
  exports: [
    AmenitiesChooseTimePageComponent,
    AmenitiesReservationsListComponent,
    AmenitiesReservationsPageComponent,
  ],
})
export class AmenitiesModule {}
