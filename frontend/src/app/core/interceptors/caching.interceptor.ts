import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';

// Define a type for cached responses
interface CachedResponse {
  response: HttpResponse<any>;
  lastUpdated: number;
}

@Injectable()
export class CachingInterceptor implements HttpInterceptor {
  private cache = new Map<string, CachedResponse>();
  private readonly ttl = 30000; // 30,000 milliseconds

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    // For non-GET requests, clear the cache for related URLs
    if (req.method !== 'GET') {
      // Clear the entire cache or specific keys
      // For simplicity, clear the entire cache:
      this.cache.clear();
      return next.handle(req);
    }

    const cacheKey = req.urlWithParams;
    const cached = this.cache.get(cacheKey);

    // If a cached response exists and it's still valid, return it
    if (cached) {
      const isExpired = Date.now() - cached.lastUpdated > this.ttl;
      if (!isExpired) {
        return of(cached.response);
      } else {
        // Remove stale entry
        this.cache.delete(cacheKey);
      }
    }

    // Otherwise, proceed with the request and cache the response if valid
    return next.handle(req).pipe(
      tap(event => {
        if (event instanceof HttpResponse && this.shouldCacheResponse(event)) {
          this.cache.set(cacheKey, {
            response: event,
            lastUpdated: Date.now(),
          });
        }
      }),
    );
  }

  private shouldCacheResponse(response: HttpResponse<any>): boolean {
    // Cache invalidation and logic
    return true;
  }
}
