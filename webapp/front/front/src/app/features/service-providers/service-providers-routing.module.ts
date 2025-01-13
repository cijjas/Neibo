import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoleGuard } from '@core/index';
import {
  ServiceProvidersDetailPageComponent,
  ServiceProvidersPageComponent,
  ServiceProvidersJoinNeighborhoodsComponent,
  FeedCreatePostPageComponent,
} from '@features/index';

import { Roles } from '@shared/models';
import { ServiceProvidersLayoutComponent } from './service-providers-layout/service-providers-layout.component';
import { ServiceProvidersCreatePostComponent } from './service-providers-create-post/service-providers-create-post.component';

const routes: Routes = [
  {
    path: '',
    component: ServiceProvidersLayoutComponent,
    children: [
      { path: '', component: ServiceProvidersPageComponent },
      { path: 'profile/:id', component: ServiceProvidersDetailPageComponent },
      {
        path: 'join-neighborhoods',
        component: ServiceProvidersJoinNeighborhoodsComponent,
        canActivate: [RoleGuard],
        data: { roles: [Roles.WORKER] },
      },
      {
        path: 'posts/new',
        component: ServiceProvidersCreatePostComponent,
        canActivate: [RoleGuard],
        data: { roles: [Roles.WORKER] },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ServiceProvidersRoutingModule { }
