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
    // Listen for navigation events
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        // When navigation starts, emit a value to cancel pending requests.
        this.cancelRequest$.next();
        // Reset subject so new requests wont be immediately cancelled.
        this.cancelRequest$ = new Subject<void>();
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
