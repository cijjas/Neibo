import { NgModule } from '@angular/core';
import { UserProfileWidgetComponent } from './user-profile-widget/user-profile-widget.component';
@NgModule({
  imports: [UserProfileWidgetComponent], // ✅ Import standalone component
  exports: [UserProfileWidgetComponent], // ✅ Export it for reuse
})
export class UserProfileBridgeModule {}
