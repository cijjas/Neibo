import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { UserProfileRoutingModule } from './user-profile-routing.module';

import { CalendarWidgetComponent } from '@features/calendar/calendar-widget/calendar-widget.component';
import { UserProfilePageComponent } from './user-profile-page/user-profile-page.component';
import { UserProfileWidgetComponent } from './user-profile-widget/user-profile-widget.component';

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
