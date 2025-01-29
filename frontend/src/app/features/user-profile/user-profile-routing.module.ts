import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserProfilePageComponent } from '@features/index';
import { userResolver } from '@shared/resolvers/user.resolver';

const routes: Routes = [
  {
    path: '',
    component: UserProfilePageComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserProfileRoutingModule {}
