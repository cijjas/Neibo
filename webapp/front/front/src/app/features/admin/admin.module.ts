import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';



import { SharedModule } from '@shared/index';
import { AdminRoutingModule } from './admin-routing.module';
import { AdminCreateAnnouncementPageComponent } from './admin-create-announcement-page/admin-create-announcement-page.component';
import { CalendarWidgetComponent } from '@features/index';
import { AdminLayoutComponent } from './admin-layout.component';


@NgModule({
    declarations: [
        AdminCreateAnnouncementPageComponent,
        AdminLayoutComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        CalendarWidgetComponent,
        AdminRoutingModule
    ],

})
export class AdminModule { }
