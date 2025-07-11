// admin-routing.module.ts
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { AdminCreateAnnouncementPageComponent } from './admin-create-announcement-page/admin-create-announcement-page.component';
import { AdminLayoutComponent } from './admin-layout/admin-layout.component';
import { AdminNeighborsRequestsPageComponent } from './admin-neighbors-requests-page/admin-neighbors-requests-page.component';
import { AdminAmenityEditPageComponent } from './admin-amenity-edit-page/admin-amenity-edit-page.component';
import { AdminServiceProvidersRequestsPageComponent } from './admin-service-providers-requests-page/admin-service-providers-requests-page.component';
import { AdminInformationPageComponent } from './admin-information-page/admin-information-page.component';
import { AdminCreateEventPageComponent } from '@features/admin/admin-create-event-page/admin-create-event-page.component';
import { AdminAmenitiesPageComponent } from './admin-amenities-page/admin-amenities-page.component';
import { AdminAmenityCreatePageComponent } from './admin-amenity-create-page/admin-amenity-create-page.component';
import { amenityResolver } from '@shared/resolvers/amenity.resolver';

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
      {
        path: 'amenities/:id/edit',
        component: AdminAmenityEditPageComponent,
        resolve: { amenity: amenityResolver },
      },
      // calendar
      { path: 'calendar/events/create', component: AdminCreateEventPageComponent },
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
