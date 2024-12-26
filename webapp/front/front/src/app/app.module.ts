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
  , ServiceProvidersPreviewComponent
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
  , NotFoundComponent
  , LoginPageComponent
  , CalendarPageComponent
  , UserProfileComponent
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
  , ServiceProvidersWidgetComponent
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
    NotFoundComponent,
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
    UserProfileComponent,
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
    ServiceProvidersWidgetComponent,
    ServiceProvidersControlBarComponent,
    ServiceProvidersPreviewComponent,
    ServiceProvidersReviewsAndPostsComponent,
    ServiceProvidersDetailPageComponent,
    ServiceProvidersEditDialogComponent,
    ServiceProvidersReviewDialogComponent,
    ServiceProvidersPreviewComponent,
    ServiceProvidersWidgetComponent,
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
