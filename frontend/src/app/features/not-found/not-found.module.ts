import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';

import { NotFoundPageComponent } from './not-found-page/not-found-page.component';
import { NotFoundRoutingModule } from './not-found-routing.module';

@NgModule({
  declarations: [NotFoundPageComponent],
  imports: [CommonModule, SharedModule, NotFoundRoutingModule],
  exports: [NotFoundPageComponent],
})
export class NotFoundModule {}
