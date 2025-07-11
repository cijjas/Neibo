import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '@shared/index';
import { AdminRoutingModule } from './admin-routing.module';
import { AdminCreateAnnouncementPageComponent } from './admin-create-announcement-page/admin-create-announcement-page.component';
import { AdminLayoutComponent } from './admin-layout/admin-layout.component';
import { AdminSidebarComponent } from './admin-sidebar/admin-sidebar.component';
import { AdminNeighborsRequestsPageComponent } from './admin-neighbors-requests-page/admin-neighbors-requests-page.component';
import { AdminAmenityEditPageComponent } from './admin-amenity-edit-page/admin-amenity-edit-page.component';
import { AdminAmenityCreatePageComponent } from './admin-amenity-create-page/admin-amenity-create-page.component';
import { AdminInformationPageComponent } from './admin-information-page/admin-information-page.component';
import { AdminCreateEventPageComponent } from '@features/admin/admin-create-event-page/admin-create-event-page.component';
import { AdminServiceProvidersRequestsPageComponent } from './admin-service-providers-requests-page/admin-service-providers-requests-page.component';
import { AdminAmenitiesPageComponent } from './admin-amenities-page/admin-amenities-page.component';
import { CalendarBridgeModule } from '@features/calendar/calendar-bridge.module';

@NgModule({
  declarations: [
    AdminLayoutComponent,
    AdminCreateAnnouncementPageComponent,
    AdminSidebarComponent,
    AdminNeighborsRequestsPageComponent,
    AdminAmenityEditPageComponent,
    AdminAmenityCreatePageComponent,
    AdminAmenitiesPageComponent,
    AdminInformationPageComponent,
    AdminCreateEventPageComponent,
    AdminServiceProvidersRequestsPageComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    CalendarBridgeModule,
    AdminRoutingModule,
  ],
})
export class AdminModule {}
