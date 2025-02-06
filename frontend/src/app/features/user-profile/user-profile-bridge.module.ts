import { NgModule } from '@angular/core';
import { UserProfileWidgetComponent } from './user-profile-widget/user-profile-widget.component';
@NgModule({
  imports: [UserProfileWidgetComponent], 
  exports: [UserProfileWidgetComponent], 
})
export class UserProfileBridgeModule {}
