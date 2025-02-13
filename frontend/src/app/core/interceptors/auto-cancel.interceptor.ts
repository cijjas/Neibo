import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NavigationStart, Router } from '@angular/router';

@Injectable()
export class AutoCancelInterceptor implements HttpInterceptor {
  private cancelRequest$ = new Subject<void>();

  constructor(private router: Router) {
    let prevUrl: string | null = null;
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        // Only cancel if user is navigating to a new URL
        // and we dont want to cancel if going to '/not-found' or an error route
        if (
          prevUrl &&
          prevUrl !== event.url &&
          !event.url.includes('/not-found')
        ) {
          this.cancelRequest$.next();
          this.cancelRequest$ = new Subject<void>();
        }
        prevUrl = event.url;
      }
    });
  }

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      // Cancel this request when cancelRequest$ emits
      takeUntil(this.cancelRequest$),
    );
  }
}
