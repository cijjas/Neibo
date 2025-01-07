// ANGULAR
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// MISC
import { AppComponent } from './app.component';
import { appRoutes } from './app.routes';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';

import { SharedModule } from '@shared/shared.module';
import { CoreModule } from '@core/core.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,

    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(appRoutes),
    NgOptimizedImage,
    ReactiveFormsModule,
    InfiniteScrollModule,
    SharedModule,
    CoreModule,
  ],

  bootstrap: [AppComponent],
})
export class AppModule {}
