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

import {
  NavbarComponent
  , SuccessToastComponent
  , LeftColumnComponent
  , WaveFooterComponent
  , BackgroundCloudsComponent
  , LandingPageNavbarComponent
  , BackgroundDrawingComponent
  , PaginatorComponent
  , RightColumnComponent
} from "@shared/index";

import {
  UserProfileWidgetComponent
  , BlogpostComponent
  , UpperFeedButtonsComponent
  , LoginDialogComponent
  , SignupDialogComponent
  , PostCardComponent
  , CalendarWidgetComponent
  , CalendarBoxComponent
  , CalendarEventsColumnComponent
  , ReservationsListComponent
  , UpperMarketplaceButtonsComponent
  , ProductCardComponent
  , UpperServiceButtonsComponent
  , ServiceProfileCardComponent
  , TabbedBoxComponent
  , ServiceProfilePageComponent
  , ReviewDialogComponent
  , EditDialogComponent
  , BlogPostServicesComponent
  , FeedComponent
  , MarketplaceComponent
  , ServicesComponent
  , InformationComponent
  , ReservationsComponent
  , NotFoundComponent
  , LoginComponent
  , CalendarComponent
  , UserProfileComponent
  , ChooseTimeComponent
  , ProductDetailComponent
  , CreatePostComponent
  , PostDetailComponent
  , ProductSellComponent
  , ProductEditComponent
  , BuyerHubComponent
  , SellerHubComponent
  , EventPageComponent
  , ListingRequestsComponent
  , ServiceProfileWidgetComponent
} from '@features/index';

import {
  AuthService,
  JwtInterceptor,
  ErrorInterceptor,
  ToastService,
} from "@core/index";


//PIPES
import { AddHoursPipe, TimeAgoPipe } from '@shared/index';


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
    ChooseTimeComponent,
    ReservationsListComponent,
    UpperMarketplaceButtonsComponent,
    ProductCardComponent,
    ProductDetailComponent,
    SuccessToastComponent,
    ProductSellComponent,
    ProductEditComponent,
    BuyerHubComponent,
    SellerHubComponent,
    ListingRequestsComponent,
    ServiceProfileWidgetComponent,
    UpperServiceButtonsComponent,
    ServiceProfileCardComponent,
    TabbedBoxComponent,
    ServiceProfilePageComponent,
    EditDialogComponent,
    ReviewDialogComponent,
    ServiceProfileCardComponent,
    ServiceProfileWidgetComponent,
    BlogPostServicesComponent,
    EventPageComponent,
    BackgroundCloudsComponent,
    BackgroundDrawingComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(routes),
    NgOptimizedImage,
    ReactiveFormsModule,
    TimeAgoPipe,
    AddHoursPipe,
    InfiniteScrollModule
  ],
  providers: [
    ToastService,
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
