// ANGULAR
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgOptimizedImage } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

//  2. Third-Party Modules (Libraries, Translate, etc.)
import {
  TranslateLoader,
  TranslateModule,
  TranslateService,
} from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

//  3. Application-Level Modules (Shared, Core, Routes)
import { SharedModule } from '@shared/shared.module';
import { CoreModule } from '@core/core.module';
import { appRoutes } from './app.routes';

//  4. Components (Specific Components at the Bottom)
import { AppComponent } from './app.component';

//  5. Environments & Configuration Functions
import { environment } from '../environments/environment';
import { PreferencesService } from '@core/services/preferences.service';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(
    http,
    environment.deployUrl + 'assets/i18n/',
    '.json',
  );
}

export function appInitializerFactory(
  translate: TranslateService,
  preferencesService: PreferencesService,
) {
  return () => {
    // Read language from preferences; if not set, default to 'en'
    const storedLanguage = preferencesService.getLanguage();
    const lang = storedLanguage || 'en';
    translate.setDefaultLang('en');
    return translate.use(lang).toPromise();
  };
}

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(appRoutes),
    NgOptimizedImage,

    SharedModule,
    CoreModule,

    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
    }),
  ],

  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: appInitializerFactory,
      deps: [TranslateService, PreferencesService],
      multi: true,
    },
  ],

  bootstrap: [AppComponent],
})
export class AppModule {}
