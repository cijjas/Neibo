import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '@shared/shared.module';
import { RejectedPageComponent } from '../rejected/rejected-page/rejected-page.component';

const routes: Routes = [{ path: '', component: RejectedPageComponent }];

@NgModule({
  declarations: [RejectedPageComponent],
  imports: [CommonModule, RouterModule.forChild(routes), SharedModule],
})
export class RejectedModule {}
