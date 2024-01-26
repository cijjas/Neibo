import { HttpClientModule } from "@angular/common/http"
import { AppComponent } from "./app.component"
import { BrowserModule } from "@angular/platform-browser"
import { NgModule } from "@angular/core"
import { AmenityService } from "./shared/services/amenity.service"
import { FormsModule } from '@angular/forms'
import { NavbarComponent} from "./components/navbar/navbar.component";
import { UserProfileWidgetComponent } from "./components/user-profile-widget/user-profile-widget.component";
import { BlogpostComponent } from "./components/blogpost/blogpost.component";
import { LeftColumnComponent } from "./components/left-column/left-column.component";
import { UpperFeedButtonsComponent } from "./components/upper-feed-buttons/upper-feed-buttons.component";
import { WaveFooterComponent } from "./components/wave-footer/wave-footer.component";
import { FeedComponent } from "./components/feed/feed.component"
import { PostComponent } from "./components/post/post.component"
import { RouterModule } from "@angular/router"
import { routes } from "./app.routes"
import { MarketplaceComponent } from "./components/marketplace/marketplace.component"
import { ServicesComponent } from "./components/services/services.component"
import { InformationComponent } from "./components/information/information.component"
import { ReservationsComponent } from "./components/reservations/reservations.component"

@NgModule({
    declarations: [
        AppComponent,
        NavbarComponent,
        UserProfileWidgetComponent,
        BlogpostComponent,
        FeedComponent,
        PostComponent,
        LeftColumnComponent,
        UpperFeedButtonsComponent,
        WaveFooterComponent,
        MarketplaceComponent,
        ServicesComponent, 
        InformationComponent,
        ReservationsComponent
    ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(routes)
  ],
    providers: [AmenityService],
    bootstrap: [AppComponent]
})
export class AppModule {}
