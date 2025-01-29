import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  AmenitiesChooseTimePageComponent,
  AmenitiesReservationsPageComponent,
} from '@features/index';

const routes: Routes = [
  { path: '', component: AmenitiesReservationsPageComponent },
  { path: 'choose-time', component: AmenitiesChooseTimePageComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AmenitiesRoutingModule {}
