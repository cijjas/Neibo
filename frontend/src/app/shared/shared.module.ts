import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { environment } from 'environments/environment';

import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

import { NavbarComponent } from './components/navbar/navbar.component';
import { SuccessToastComponent } from './components/success-toast/success-toast.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { WaveFooterComponent } from './components/wave-footer/wave-footer.component';
import { BackgroundCloudsComponent } from './components/background-clouds/background-clouds.component';
import { LandingPageNavbarComponent } from './components/landing-page-navbar/landing-page-navbar.component';
import { BackgroundDrawingComponent } from './components/background-drawing/background-drawing.component';
import { PaginatorComponent } from './components/paginator/paginator.component';
import { TagsFilterWidgetComponent } from './components/tags-filter-widget/tags-filter-widget.component';
import { InfiniteScrollSelectComponent } from './components/infinite-scroll-select/infinite-scroll-select.component';
import { ConfirmationDialogComponent } from './components/confirmation-dialog/confirmation-dialog.component';
import { PlaceholderPostComponent } from './components/placeholder-post/placeholder-post.component';
import { PlaceholderServiceProviderComponent } from './components/placeholder-service-provider/placeholder-service-provider.component';
import { PlaceholderProductComponent } from './components/placeholder-product/placeholder-product.component';
import { FormErrorComponent } from './components/form-error/form-error.component';
import { PublicLayoutComponent } from './layouts/public-layout/public-layout.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { CalendarBridgeModule } from '@features/calendar/calendar-bridge.module';
import { UserProfileBridgeModule } from '@features/user-profile/user-profile-bridge.module';
import { createTranslateLoader } from 'app/app.module';

@NgModule({
  declarations: [
    NavbarComponent,
    PaginatorComponent,
    SuccessToastComponent,
    SidebarComponent,
    WaveFooterComponent,
    BackgroundCloudsComponent,
    LandingPageNavbarComponent,
    BackgroundDrawingComponent,
    TagsFilterWidgetComponent,
    ConfirmationDialogComponent,
    InfiniteScrollSelectComponent,
    PublicLayoutComponent,
    MainLayoutComponent,
    PlaceholderPostComponent,
    PlaceholderServiceProviderComponent,
    PlaceholderProductComponent,
    FormErrorComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    CalendarBridgeModule,
    UserProfileBridgeModule,
    TranslateModule.forChild({
      loader: {
        provide: TranslateLoader,
        useFactory: createTranslateLoader,
        deps: [HttpClient],
      },
    }),
  ],
  exports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    NavbarComponent,
    PaginatorComponent,
    SuccessToastComponent,
    SidebarComponent,
    WaveFooterComponent,
    BackgroundCloudsComponent,
    LandingPageNavbarComponent,
    BackgroundDrawingComponent,
    TagsFilterWidgetComponent,
    InfiniteScrollSelectComponent,
    ConfirmationDialogComponent,
    TranslateModule,
    PlaceholderPostComponent,
    PlaceholderServiceProviderComponent,
    PlaceholderProductComponent,
    CalendarBridgeModule,
    UserProfileBridgeModule,
    FormErrorComponent,
  ],
})
export class SharedModule {}
