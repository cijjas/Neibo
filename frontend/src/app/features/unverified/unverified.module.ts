import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';

import { RouterModule, Routes } from '@angular/router';
import { UnverifiedPageComponent } from './unverified-page/unverified-page.component';

const routes: Routes = [{ path: '', component: UnverifiedPageComponent }];

@NgModule({
  declarations: [UnverifiedPageComponent],
  imports: [CommonModule, SharedModule, RouterModule.forChild(routes)],
})
export class UnverifiedModule {}
