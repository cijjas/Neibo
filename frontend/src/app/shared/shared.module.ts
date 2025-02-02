import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router'; // For directives like routerLink
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { environment } from 'environments/environment';

// Import shared components and pipes
import {
  NavbarComponent,
  SuccessToastComponent,
  SidebarComponent,
  WaveFooterComponent,
  BackgroundCloudsComponent,
  LandingPageNavbarComponent,
  BackgroundDrawingComponent,
  PaginatorComponent,
  RightColumnComponent,
  TagsFilterWidgetComponent,
  InfiniteScrollSelectComponent,
  ConfirmationDialogComponent,
  PlaceholderPostComponent,
  PlaceholderServiceProviderComponent,
  PlaceholderProductComponent,
} from '@shared/index';

import { TimeAgoPipe, AddHoursPipe } from '@shared/index';
import {
  CalendarWidgetComponent,
  UserProfileWidgetComponent,
} from '@features/index';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { PublicLayoutComponent } from './layouts/public-layout/public-layout.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { Environment } from '@angular/cli/lib/config/workspace-schema';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(
    http,
    environment.deployUrl + 'assets/i18n/',
    '.json',
  );
}

@NgModule({
  declarations: [
    NavbarComponent,
    PaginatorComponent,
    SuccessToastComponent,
    SidebarComponent,
    WaveFooterComponent,
    BackgroundCloudsComponent,
    LandingPageNavbarComponent,
    RightColumnComponent,
    BackgroundDrawingComponent,
    TagsFilterWidgetComponent,
    ConfirmationDialogComponent,
    InfiniteScrollSelectComponent,
    PublicLayoutComponent,
    MainLayoutComponent,
    PlaceholderPostComponent,
    PlaceholderServiceProviderComponent,
    PlaceholderProductComponent,
  ],
  imports: [
    CommonModule,
    RouterModule, // Add this for routerLink and related directives
    FormsModule,
    ReactiveFormsModule,
    TimeAgoPipe,
    AddHoursPipe,
    CalendarWidgetComponent,
    UserProfileWidgetComponent,
    TranslateModule.forChild({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
    }),
  ],
  exports: [
    CommonModule,
    RouterModule, // Export this to make router directives available in other modules
    FormsModule,
    ReactiveFormsModule,
    NavbarComponent,
    PaginatorComponent,
    SuccessToastComponent,
    SidebarComponent,
    WaveFooterComponent,
    BackgroundCloudsComponent,
    LandingPageNavbarComponent,
    RightColumnComponent,
    BackgroundDrawingComponent,
    TagsFilterWidgetComponent,
    InfiniteScrollSelectComponent,
    ConfirmationDialogComponent,
    TimeAgoPipe,
    AddHoursPipe,
    TranslateModule,
    PlaceholderPostComponent,
    PlaceholderServiceProviderComponent,
    PlaceholderProductComponent,
  ],
})
export class SharedModule {}
