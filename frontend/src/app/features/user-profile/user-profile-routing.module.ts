import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserProfilePageComponent } from '@features/index';

const routes: Routes = [
    { path: ':id', component: UserProfilePageComponent },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class UserProfileRoutingModule { }
