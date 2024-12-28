import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';



import { SharedModule } from '@shared/index';
import { AdminRoutingModule } from './admin-routing.module';
import { AdminCreateAnnouncementPageComponent } from './admin-create-announcement-page/admin-create-announcement-page.component';
import { CalendarWidgetComponent } from '@features/index';
import { AdminLayoutComponent } from './admin-layout.component';
import { AdminSidebarComponent } from './admin-sidebar/admin-sidebar.component';
import { AdminNeighborsRequestsPageComponent } from './admin-neighbors-requests-page/admin-neighbors-requests-page.component';
import { AdminAmenityEditPageComponent } from './admin-amenity-edit-page/admin-amenity-edit-page.component';
import { AdminAmenityCreatePageComponent } from './admin-amenity-create-page/admin-amenity-create-page.component';
import { AdminInformationPageComponent } from './admin-information-page/admin-information-page.component';
import { AdminContactCreatePageComponent } from './admin-contact-create-page/admin-contact-create-page.component';
import { AdminCreateResourcePageComponent } from './admin-create-resource-page/admin-create-resource-page.component';
import { AdminCreateEventComponent } from './admin-create-event/admin-create-event.component';


@NgModule({
    declarations: [
        AdminLayoutComponent,
        AdminCreateAnnouncementPageComponent,
        AdminSidebarComponent,
        AdminNeighborsRequestsPageComponent,
        AdminAmenityEditPageComponent,
        AdminAmenityCreatePageComponent,
        AdminInformationPageComponent,
        AdminContactCreatePageComponent,
        AdminCreateResourcePageComponent,
        AdminCreateEventComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        CalendarWidgetComponent,
        AdminRoutingModule,

    ],

})
export class AdminModule { }
