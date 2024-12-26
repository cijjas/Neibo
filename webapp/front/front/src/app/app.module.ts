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
  , FeedPostPreviewComponent
  , FeedControlBarComponent
  , LoginDialogComponent
  , SignupDialogComponent
  , FeedPostContentComponent
  , CalendarWidgetComponent
  , CalendarBoxComponent
  , CalendarEventsColumnComponent
  , AmenitiesReservationsListComponent
  , MarketplaceControlBarComponent
  , MarketplaceProductPreviewComponent
  , ServiceProvidersControlBarComponent
  , ServiceProvidersContentComponent
  , ServiceProvidersReviewsAndPostsComponent
  , ServiceProvidersDetailPageComponent
  , ServiceProvidersReviewDialogComponent
  , ServiceProvidersEditDialogComponent
  , ServiceProvidersPostPreviewComponent
  , FeedPageComponent
  , MarketplacePageComponent
  , ServiceProvidersPageComponent
  , InformationPageComponent
  , AmenitiesReservationsPageComponent
  , NotFoundPageComponent
  , LoginPageComponent
  , CalendarPageComponent
  , UserProfilePageComponent
  , AmenitiesChooseTimePageComponent
  , MarketplaceProductDetailPageComponent
  , FeedCreatePostPageComponent
  , FeedPostDetailPageComponent
  , MarketplaceProductSellPageComponent
  , MarketplaceProductEditPageComponent
  , MarketplaceDashboardBuyerPageComponent
  , MarketplaceDashboardSellerPageComponent
  , CalendarEventPageComponent
  , MarketplaceProductRequestsPageComponent
  , ServiceProvidersPreviewComponent
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
    FeedPostPreviewComponent,
    FeedPageComponent,
    FeedPostDetailPageComponent,
    LeftColumnComponent,
    FeedControlBarComponent,
    WaveFooterComponent,
    MarketplacePageComponent,
    ServiceProvidersPageComponent,
    InformationPageComponent,
    AmenitiesReservationsPageComponent,
    LandingPageNavbarComponent,
    NotFoundPageComponent,
    LoginPageComponent,
    LoginDialogComponent,
    SignupDialogComponent,
    FeedPostContentComponent,
    PaginatorComponent,
    RightColumnComponent,
    FeedCreatePostPageComponent,
    CalendarWidgetComponent,
    CalendarPageComponent,
    CalendarBoxComponent,
    CalendarEventsColumnComponent,
    UserProfilePageComponent,
    AmenitiesChooseTimePageComponent,
    AmenitiesReservationsListComponent,
    MarketplaceControlBarComponent,
    MarketplaceProductPreviewComponent,
    MarketplaceProductDetailPageComponent,
    SuccessToastComponent,
    MarketplaceProductSellPageComponent,
    MarketplaceProductEditPageComponent,
    MarketplaceDashboardBuyerPageComponent,
    MarketplaceDashboardSellerPageComponent,
    MarketplaceProductRequestsPageComponent,
    ServiceProvidersPreviewComponent,
    ServiceProvidersControlBarComponent,
    ServiceProvidersContentComponent,
    ServiceProvidersReviewsAndPostsComponent,
    ServiceProvidersDetailPageComponent,
    ServiceProvidersEditDialogComponent,
    ServiceProvidersReviewDialogComponent,
    ServiceProvidersContentComponent,
    ServiceProvidersPreviewComponent,
    ServiceProvidersPostPreviewComponent,
    CalendarEventPageComponent,
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
