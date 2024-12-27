import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
    ServiceProvidersDetailPageComponent,
    ServiceProvidersPageComponent
} from '@features/index';

const routes: Routes = [
    { path: '', component: ServiceProvidersPageComponent },
    { path: 'profile/:id', component: ServiceProvidersDetailPageComponent },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class ServiceProvidersRoutingModule { }
