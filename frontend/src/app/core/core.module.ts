import { APP_INITIALIZER, NgModule, Optional, SkipSelf } from '@angular/core';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

import { AuthService } from './services/auth.service';
import { AppInitService } from './services/app-init.service';
import { ToastService } from './services/toast.service';
import { JwtInterceptor } from './interceptors/jwt.interceptor';
import { ErrorInterceptor } from './interceptors/error.interceptor';
import { ConfirmationService } from './services/confirmation.service';
import { AutoCancelInterceptor } from './interceptors/auto-cancel.interceptor';

export function initApp(appInitService: AppInitService) {
  return () => appInitService.loadInitialLinks();
}

@NgModule({
  imports: [],
  providers: [
    AuthService,
    ToastService,
    ConfirmationService,
    AppInitService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AutoCancelInterceptor,
      multi: true,
    },
    {
      provide: APP_INITIALIZER,
      useFactory: initApp,
      deps: [AppInitService],
      multi: true,
    },
  ],
})
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    if (parentModule) {
      throw new Error(
        'CoreModule is already loaded. Import it in AppModule only.',
      );
    }
  }
}
