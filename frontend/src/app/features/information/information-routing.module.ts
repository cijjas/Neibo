import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InformationPageComponent } from './information-page/information-page.component';

const routes: Routes = [{ path: '', component: InformationPageComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class InformationRoutingModule {}
