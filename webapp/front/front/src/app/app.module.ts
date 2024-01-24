import { HttpClientModule } from "@angular/common/http"
import { AppComponent } from "./app.component"
import { BrowserModule } from "@angular/platform-browser"
import { NgModule } from "@angular/core"
import { AmenityService } from "./shared/services/amenity.service"
import { FormsModule } from '@angular/forms'
import {NavbarComponent} from "./components/navbar/navbar.component";
import {UserProfileWidgetComponent} from "./components/user-profile-widget/user-profile-widget.component";
import {BlogpostComponent} from "./components/blogpost/blogpost.component";
import {LeftColumnComponent} from "./components/left-column/left-column.component";

@NgModule({
    declarations: [
        AppComponent,
        NavbarComponent,
        UserProfileWidgetComponent,
      BlogpostComponent

    ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    LeftColumnComponent
  ],
    providers: [AmenityService],
    bootstrap: [AppComponent]
})
export class AppModule {}
