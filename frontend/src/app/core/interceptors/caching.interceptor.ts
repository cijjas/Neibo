import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap, shareReplay, finalize } from 'rxjs/operators';

interface CachedResponse {
  response: HttpResponse<any>;
  lastUpdated: number;
}

@Injectable()
export class CachingInterceptor implements HttpInterceptor {
  private cache = new Map<string, CachedResponse>();
  private inFlightRequests = new Map<string, Observable<HttpEvent<any>>>();
  private readonly ttl = 30000; // 30 seconds
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    // Only cache GET requests.
    if (req.method !== 'GET') {
      // Select which requests clear the cache.
      this.cache.clear();
      this.inFlightRequests.clear();
      return next.handle(req);
    }

    // URL with params as the cache key.
    const cacheKey = req.urlWithParams;

    // If there is a cached response that hasn’t expired, return it.
    const cached = this.cache.get(cacheKey);
    if (cached) {
      const isExpired = Date.now() - cached.lastUpdated > this.ttl;
      if (!isExpired) {
        return of(cached.response);
      } else {
        // Remove the stale cache entry.
        this.cache.delete(cacheKey);
      }
    }

    // If a request for the same URL is already in-flight, return that observable.
    if (this.inFlightRequests.has(cacheKey)) {
      return this.inFlightRequests.get(cacheKey)!;
    }

    // Forward the request and store the observable in the in-flight map.
    const request$ = next.handle(req).pipe(
      // Share the observable among multiple subscribers.
      shareReplay(1),
      tap(event => {
        // Only cache successful HTTP responses.
        if (
          event instanceof HttpResponse &&
          this.shouldCacheResponse(req, event)
        ) {
          this.cache.set(cacheKey, {
            response: event,
            lastUpdated: Date.now(),
          });
        }
      }),
      // Once the request completes (success or error), remove it from the in-flight map.
      finalize(() => {
        this.inFlightRequests.delete(cacheKey);
      }),
    );

    // Store the in-flight request observable.
    this.inFlightRequests.set(cacheKey, request$);
    return request$;
  }

  /**
   * Determines whether the response should be cached.
   * Can be changed tp accept responses with 200 code.
   */
  private shouldCacheResponse(
    req: HttpRequest<any>,
    response: HttpResponse<any>,
  ): boolean {
    // Example: do not cache if Cache-Control header says 'no-store'
    if (req.headers.get('Cache-Control') === 'no-store') {
      return false;
    }
    // Example: only cache successful responses (HTTP 200 OK)
    return response.status === 200;
  }
}
