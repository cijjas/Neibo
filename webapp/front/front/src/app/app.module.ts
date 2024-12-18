// ANGULAR
import { HTTP_INTERCEPTORS, HttpClientModule } from "@angular/common/http";
import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { NgOptimizedImage } from "@angular/common";
import { RouterModule } from "@angular/router";
import { FormsModule, ReactiveFormsModule } from '@angular/forms'

// MISC
import { AppComponent } from "./app.component";
import { routes } from "./app.routes"
import { InfiniteScrollModule } from 'ngx-infinite-scroll';

// COMPONENTS
import { NavbarComponent } from "./components/navbar/navbar.component";
import { UserProfileWidgetComponent } from "./components/user-profile-widget/user-profile-widget.component";
import { BlogpostComponent } from "./components/blogpost/blogpost.component";
import { LeftColumnComponent } from "./components/left-column/left-column.component";
import { UpperFeedButtonsComponent } from "./components/upper-feed-buttons/upper-feed-buttons.component";
import { WaveFooterComponent } from "./components/wave-footer/wave-footer.component";
import { PostDetailComponent } from "./components/post-detail/post-detail.component"
import { BackgroundCloudsComponent } from "./components/background-clouds/background-clouds.component";
import { LandingPageNavbarComponent } from './components/landing-page-navbar/landing-page-navbar.component';
import { BackgroundDrawingComponent } from "./components/background-drawing/background-drawing.component";
import { LoginDialogComponent } from './components/auth-dialogs/login-dialog/login-dialog.component';
import { SignupDialogComponent } from './components/auth-dialogs/signup-dialog/signup-dialog.component';
import { PostCardComponent } from "./components/post-card/post-card.component";
import { PaginatorComponent } from "./components/paginator/paginator.component";
import { RightColumnComponent } from "./components/right-column/right-column.component";
import { CalendarWidgetComponent } from './components/calendar-widget/calendar-widget.component'
import { CreatePostComponent } from "./components/create-post/create-post.component";
import { CalendarBoxComponent } from "./components/calendar-box/calendar-box.component";
import { CalendarEventsColumnComponent } from "./components/calendar-events-column/calendar-events-column.component";

// MODULES
import { FeedComponent } from "./modules/feed/feed.component"
import { MarketplaceComponent } from "./modules/marketplace/marketplace.component"
import { ServicesComponent } from "./modules/services/services.component"
import { InformationComponent } from "./modules/information/information.component"
import { ReservationsComponent } from "./modules/reservations/reservations.component"
import { NotFoundComponent } from './modules/not-found/not-found.component';
import { LoginComponent } from './modules/auth/login/login.component';
import { CalendarComponent } from './modules/calendar/calendar.component';
import { UserProfileComponent } from "./modules/user-profile/user-profile.component";
import { ChooseTimeComponent } from "./modules/choose-time/choose-time.component";

// SERVICES
import { AuthService } from "./shared/services/index.service";
import { JwtInterceptor } from "./shared/interceptors/jwt.interceptor";
import { ErrorInterceptor } from "./shared/interceptors/error.interceptor";

//PIPES
import { AddHoursPipe } from './pipes/add-hours/add-hours.pipe';
import { TimeAgoPipe } from "./pipes/time-ago/time-ago.pipe";


@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    UserProfileWidgetComponent,
    BlogpostComponent,
    FeedComponent,
    PostDetailComponent,
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
    PostCardComponent,
    PaginatorComponent,
    RightColumnComponent,
    CreatePostComponent,
    CalendarWidgetComponent,
    CalendarComponent,
    CalendarBoxComponent,
    CalendarEventsColumnComponent,
    UserProfileComponent,
    ChooseTimeComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(routes),
    BackgroundCloudsComponent,
    NgOptimizedImage,
    BackgroundDrawingComponent,
    ReactiveFormsModule,
    TimeAgoPipe,
    AddHoursPipe,
    InfiniteScrollModule
  ],
  providers: [
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
