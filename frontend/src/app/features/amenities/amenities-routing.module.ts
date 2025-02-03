import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AmenitiesChooseTimePageComponent } from './amenities-choose-time-page/amenities-choose-time-page.component';
import { AmenitiesReservationsPageComponent } from './amenities-reservations-page/amenities-reservations-page.component';

const routes: Routes = [
  { path: '', component: AmenitiesReservationsPageComponent },
  { path: 'choose-time', component: AmenitiesChooseTimePageComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AmenitiesRoutingModule {}
