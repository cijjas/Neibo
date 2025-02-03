import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';

import { RouterModule, Routes } from '@angular/router';
import { RejectedPageComponent } from './rejected-page/rejected-page.component';

const routes: Routes = [{ path: '', component: RejectedPageComponent }];

@NgModule({
  declarations: [RejectedPageComponent],
  imports: [CommonModule, SharedModule, RouterModule.forChild(routes)],
})
export class RejectedModule {}
