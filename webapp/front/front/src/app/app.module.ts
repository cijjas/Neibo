// ANGULAR
import { HTTP_INTERCEPTORS, HttpClientModule } from "@angular/common/http";
import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { NgOptimizedImage } from "@angular/common";
import { RouterModule } from "@angular/router";
import { FormsModule, ReactiveFormsModule } from '@angular/forms'

// MISC
import { AppComponent } from "./app.component";
import { appRoutes } from "./app.routes"
import { InfiniteScrollModule } from 'ngx-infinite-scroll';


import {
  AmenitiesModule,
  AuthModule,
  InformationModule,
  FeedModule,
  MarketplaceModule,
  NotFoundModule,
  ServiceProvidersModule,
  UserProfileModule
} from '@features/index';





import { SharedModule } from '@shared/shared.module';
import { CoreModule } from "@core/core.module";
import { CalendarModule } from "@features/index";

@NgModule({
  declarations: [
    AppComponent,

  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(appRoutes),
    NgOptimizedImage,
    ReactiveFormsModule,
    InfiniteScrollModule,
    SharedModule,
    CoreModule


  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
