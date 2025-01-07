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

const routes: Routes = [
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
    component: FeedCreatePostPageComponent,
    canActivate: [RoleGuard],
    data: { roles: [Roles.WORKER] },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ServiceProvidersRoutingModule {}
