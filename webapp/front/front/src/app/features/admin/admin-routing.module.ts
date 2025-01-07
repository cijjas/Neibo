// admin-routing.module.ts
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { AdminCreateAnnouncementPageComponent } from './admin-create-announcement-page/admin-create-announcement-page.component';
import { AdminLayoutComponent } from './admin-layout.component';
import { AdminNeighborsRequestsPageComponent } from './admin-neighbors-requests-page/admin-neighbors-requests-page.component';
import { AdminAmenityEditPageComponent } from './admin-amenity-edit-page/admin-amenity-edit-page.component';
import { AdminServiceProvidersRequestsPageComponent } from './admin-service-providers-requests-page/admin-service-providers-requests-page.component';
import { AdminInformationPageComponent } from './admin-information-page/admin-information-page.component';
import { AdminCreateEventComponent } from './admin-create-event/admin-create-event.component';
import { AdminAmenitiesPageComponent } from './admin-amenities-page/admin-amenities-page.component';
import { AdminAmenityCreatePageComponent } from './admin-amenity-create-page/admin-amenity-create-page.component';

const routes: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      { path: '', redirectTo: 'announcement/new', pathMatch: 'full' },

      {
        path: 'announcement/new',
        component: AdminCreateAnnouncementPageComponent,
      },
      // neighbors and services
      {
        path: 'neighbors/requests',
        component: AdminNeighborsRequestsPageComponent,
      },
      { path: 'neighbors', component: AdminNeighborsRequestsPageComponent },
      {
        path: 'service-providers/requests',
        component: AdminServiceProvidersRequestsPageComponent,
      },
      {
        path: 'service-providers',
        component: AdminServiceProvidersRequestsPageComponent,
      },
      // amenities
      { path: 'amenities', component: AdminAmenitiesPageComponent },
      { path: 'amenities/new', component: AdminAmenityCreatePageComponent },
      { path: 'amenities/:id/edit', component: AdminAmenityEditPageComponent },
      // calendar
      { path: 'calendar/events/create', component: AdminCreateEventComponent },
      // information
      { path: 'information', component: AdminInformationPageComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}
