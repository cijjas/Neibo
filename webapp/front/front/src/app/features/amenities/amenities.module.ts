import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
    AmenitiesChooseTimePageComponent,
    AmenitiesReservationsListComponent,
    AmenitiesReservationsPageComponent
} from '@features/index';

import { SharedModule } from '@shared/shared.module';
import { CalendarWidgetComponent } from '@features/index';
import { AmenitiesRoutingModule } from './amenities-routing.module';

@NgModule({
    declarations: [
        AmenitiesChooseTimePageComponent,
        AmenitiesReservationsListComponent,
        AmenitiesReservationsPageComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        CalendarWidgetComponent,
        AmenitiesRoutingModule
    ],
    exports: [
        AmenitiesChooseTimePageComponent,
        AmenitiesReservationsListComponent,
        AmenitiesReservationsPageComponent
    ]
})
export class AmenitiesModule { }
