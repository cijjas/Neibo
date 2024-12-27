import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InformationPageComponent } from '@features/index';
import { SharedModule } from '@shared/shared.module';

import { CalendarWidgetComponent, InformationRoutingModule } from '@features/index';


@NgModule({
    declarations: [InformationPageComponent],
    imports: [
        CommonModule,
        SharedModule,
        CalendarWidgetComponent,
        InformationRoutingModule
    ],
    exports: [InformationPageComponent]
})
export class InformationModule { }
