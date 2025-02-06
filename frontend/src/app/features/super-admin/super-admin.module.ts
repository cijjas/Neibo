import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';

import { RouterModule, Routes } from '@angular/router';
import { SuperAdminPageComponent } from './super-admin-page/super-admin-page.component';

const routes: Routes = [{ path: '', component: SuperAdminPageComponent }];

@NgModule({
  declarations: [SuperAdminPageComponent],
  imports: [CommonModule, SharedModule, RouterModule.forChild(routes)],
})
export class SuperAdminModule {}
