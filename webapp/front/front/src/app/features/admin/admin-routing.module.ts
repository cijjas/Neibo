// admin-routing.module.ts
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { AdminCreateAnnouncementPageComponent } from './admin-create-announcement-page/admin-create-announcement-page.component';
import { AdminLayoutComponent } from './admin-layout.component';
import { AdminNeighborsRequestsPageComponent } from './admin-neighbors-requests-page/admin-neighbors-requests-page.component';

const routes: Routes = [
    {
        path: '',
        component: AdminLayoutComponent, // Shared layout
        children: [
            { path: 'new', component: AdminCreateAnnouncementPageComponent },
            { path: 'neighbors/requests', component: AdminNeighborsRequestsPageComponent },
            { path: 'neighbors', component: AdminCreateAnnouncementPageComponent },
            { path: 'service-providers/requests', component: AdminCreateAnnouncementPageComponent },
            { path: 'service-providers', component: AdminCreateAnnouncementPageComponent },
            { path: 'amenities', component: AdminCreateAnnouncementPageComponent },
            { path: 'amenities/new', component: AdminCreateAnnouncementPageComponent },
            { path: 'amenities/edit', component: AdminCreateAnnouncementPageComponent },
            { path: 'calendar/events/new', component: AdminCreateAnnouncementPageComponent },
            { path: 'information', component: AdminCreateAnnouncementPageComponent },
            { path: 'information/contact-info/new', component: AdminCreateAnnouncementPageComponent },
            { path: 'information/resource-info/new', component: AdminCreateAnnouncementPageComponent },
        ]
    }
];


@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class AdminRoutingModule { }
