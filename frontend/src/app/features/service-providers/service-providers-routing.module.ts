import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoleGuard } from '@core/index';
import { ServiceProvidersDetailPageComponent } from './service-providers-detail-page/service-providers-detail-page.component';
import { ServiceProvidersPageComponent } from './service-providers-page/service-providers-page.component';
import { ServiceProvidersJoinNeighborhoodsPageComponent } from './service-providers-join-neighborhoods-page/service-providers-join-neighborhoods-page.component';
import { ServiceProvidersLayoutComponent } from './service-providers-layout/service-providers-layout.component';
import { ServiceProvidersCreatePostComponent } from './service-providers-create-post/service-providers-create-post.component';

import { Role } from '@shared/models';
import { serviceProviderResolver } from '@shared/resolvers/service-provider.resolver';

const routes: Routes = [
  {
    path: '',
    component: ServiceProvidersLayoutComponent,
    children: [
      { path: '', component: ServiceProvidersPageComponent },
      {
        path: 'profiles/:id',
        component: ServiceProvidersDetailPageComponent,
        resolve: { worker: serviceProviderResolver },
      },
      {
        path: 'join-neighborhoods',
        component: ServiceProvidersJoinNeighborhoodsPageComponent,
        canActivate: [RoleGuard],
        data: { roles: [Role.WORKER] },
      },
      {
        path: 'posts/new',
        component: ServiceProvidersCreatePostComponent,
        canActivate: [RoleGuard],
        data: { roles: [Role.WORKER] },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ServiceProvidersRoutingModule {}
