import { HTTP_INTERCEPTORS, HttpClientModule } from "@angular/common/http"
import { AppComponent } from "./app.component"
import { BrowserModule } from "@angular/platform-browser"
import { NgModule } from "@angular/core"
import { AmenityService } from "./shared/services/amenity.service"
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { NavbarComponent } from "./components/navbar/navbar.component";
import { UserProfileWidgetComponent } from "./components/user-profile-widget/user-profile-widget.component";
import { BlogpostComponent } from "./components/blogpost/blogpost.component";
import { LeftColumnComponent } from "./components/left-column/left-column.component";
import { UpperFeedButtonsComponent } from "./components/upper-feed-buttons/upper-feed-buttons.component";
import { WaveFooterComponent } from "./components/wave-footer/wave-footer.component";
import { FeedComponent } from "./modules/feed/feed.component"
import { PostComponent } from "./components/post/post.component"
import { RouterModule } from "@angular/router"
import { routes } from "./app.routes"
import { BackgroundCloudsComponent } from "./components/background-clouds/background-clouds.component";
import { MarketplaceComponent } from "./modules/marketplace/marketplace.component"
import { ServicesComponent } from "./modules/services/services.component"
import { InformationComponent } from "./modules/information/information.component"
import { ReservationsComponent } from "./modules/reservations/reservations.component"
import { NgOptimizedImage } from "@angular/common";
import { TimeAgoPipe } from "./pipes/time-ago/time-ago.pipe";
import { LandingPageNavbarComponent } from './components/landing-page-navbar/landing-page-navbar.component';
import { NotFoundComponent } from './modules/not-found/not-found.component';
import { BackgroundDrawingComponent } from "./components/background-drawing/background-drawing.component";
import { LoginComponent } from './modules/auth/login/login.component';
import { LoginDialogComponent } from './components/auth-dialogs/login-dialog/login-dialog.component';
import { SignupDialogComponent } from './components/auth-dialogs/signup-dialog/signup-dialog.component';
import { AuthService } from "./shared/services/auth.service";
import { JwtInterceptor } from "./shared/interceptors/jwt.interceptor";
import { ErrorInterceptor } from "./shared/interceptors/error.interceptor";

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
    ReservationsComponent,
    LandingPageNavbarComponent,
    NotFoundComponent,
    LoginComponent,
    LoginDialogComponent,
    SignupDialogComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(routes),
    BackgroundCloudsComponent,
    NgOptimizedImage,
    TimeAgoPipe,
    BackgroundDrawingComponent,
    ReactiveFormsModule
  ],
  providers: [
    AmenityService,
    AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true
    }
  ],
  exports: [
    LandingPageNavbarComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
