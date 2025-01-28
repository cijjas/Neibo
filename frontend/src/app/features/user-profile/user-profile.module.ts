import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  CalendarWidgetComponent,
  UserProfilePageComponent,
  UserProfileWidgetComponent,
} from '@features/index';

import { SharedModule } from '@shared/shared.module';
import { UserProfileRoutingModule } from './user-profile-routing.module';

@NgModule({
  declarations: [UserProfilePageComponent],
  imports: [
    CommonModule,
    SharedModule,
    UserProfileWidgetComponent,
    UserProfileRoutingModule,
    CalendarWidgetComponent,
  ],
  exports: [UserProfilePageComponent, UserProfileWidgetComponent],
})
export class UserProfileModule {}
