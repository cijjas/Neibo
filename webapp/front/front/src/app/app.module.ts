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
import {UpperFeedButtonsComponent} from "./components/upper-feed-buttons/upper-feed-buttons.component";
import {WaveFooterComponent} from "./components/wave-footer/wave-footer.component";

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
    LeftColumnComponent,
    UpperFeedButtonsComponent,
    WaveFooterComponent
  ],
    providers: [AmenityService],
    bootstrap: [AppComponent]
})
export class AppModule {}
