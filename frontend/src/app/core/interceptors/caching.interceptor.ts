import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, of } from 'rxjs';
import {
  tap,
  shareReplay,
  finalize,
  switchMap,
  catchError,
} from 'rxjs/operators';

interface CachedResponse {
  response: HttpResponse<any>;
  etag?: string;
  maxAge?: number;
  timeCached: number;
}

interface CacheDirectives {
  maxAge?: number; // in seconds
  noStore?: boolean;
  noCache?: boolean;
  mustRevalidate?: boolean;
  [key: string]: any;
}

@Injectable()
export class CachingInterceptor implements HttpInterceptor {
  private cache = new Map<string, CachedResponse>();
  private inFlightRequests = new Map<string, Observable<HttpEvent<any>>>();

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    // We only want to cache GET requests.
    if (req.method !== 'GET') {
      return next.handle(req);
    }

    // Use the complete URL (including params) as the cache key.
    const cacheKey = req.urlWithParams;
    // If there's an in-flight request for the same key, return it.
    if (this.inFlightRequests.has(cacheKey)) {
      return this.inFlightRequests.get(cacheKey)!;
    }

    // Check if we already have it in cache.
    const cached = this.cache.get(cacheKey);

    if (cached) {
      const isFresh = this.isCacheFresh(cached);
      if (isFresh) {
        // It's still fresh (i.e., within max-age). Serve directly.
        return of(cached.response);
      } else if (cached.etag) {
        // We have an ETag and the cache is stale => Perform a conditional GET
        // by including If-None-Match
        const conditionalReq = req.clone({
          setHeaders: { 'If-None-Match': cached.etag },
        });

        // Make the request  and store it in flgiht so that multiple subscribers share it.
        const request$ = next.handle(conditionalReq).pipe(
          // We only want to share final responses.
          shareReplay(1),
          switchMap(event => {
            if (event instanceof HttpResponse) {
              // If the server returns 304 Not Modified, we can reuse the old response body
              // but update timestamps/headers accordingly. If 200, we store the new data.
              if (event.status === 304) {
                // Not modified => update the cache timestamp but keep the old response body.
                this.updateTimestampOnly(cacheKey);
                // Emit the old cached response with updated haewders, if you want to merge them.
                // Or simply return the cached.response (often enough).
                return of(this.cache.get(cacheKey)!.response);
              } else {
                // 200 or something else => update the cache with new response
                this.updateCache(cacheKey, event);
                return of(event);
              }
            }
            // If its not an HttpResponse, just pass it along
            return of(event);
          }),
          catchError(error => {
            // If theres an error, we might still want to fallback to the cached version,
            if (error instanceof HttpErrorResponse && cached.response) {
              // if server is unreachable fallback to the stale cache:
              return of(cached.response);
            }
            // Otherwise, rethrow the error.
            throw error;
          }),
          finalize(() => {
            this.inFlightRequests.delete(cacheKey);
          }),
        );

        this.inFlightRequests.set(cacheKey, request$);
        return request$;
      } else {
        // Theres a cached entry but no ETag to revalidate => we simply fall through
        // and do a normal request (and re-cache).
        this.cache.delete(cacheKey);
      }
    }

    // If no cached entry OR  a normal request, just do it:
    const request$ = next.handle(req).pipe(
      shareReplay(1),
      tap(event => {
        if (event instanceof HttpResponse && event.status === 200) {
          this.updateCache(cacheKey, event);
        }
      }),
      finalize(() => {
        this.inFlightRequests.delete(cacheKey);
      }),
    );

    // Store the inflight request so other subscribers wait.
    this.inFlightRequests.set(cacheKey, request$);
    return request$;
  }

  /**
   * Update cache content with a fresh 200 response (parse headers for ETag, max-age).
   */
  private updateCache(cacheKey: string, response: HttpResponse<any>): void {
    const etag = response.headers.get('ETag') || undefined;
    const cacheControl = response.headers.get('Cache-Control') || '';
    const directives = this.parseCacheControlHeader(cacheControl);

    // Build a new CachedResponse object
    const cachedResponse: CachedResponse = {
      response,
      etag,
      maxAge: directives.maxAge,
      timeCached: Date.now(),
    };

    this.cache.set(cacheKey, cachedResponse);
  }

  /**
   * When a 304 Not Modified is returned, we typically just need to update the "timeCached"
   * so it's fresh again. We could also merge new headers from the 304 if needed.
   */
  private updateTimestampOnly(cacheKey: string): void {
    const cached = this.cache.get(cacheKey);
    if (!cached) return;
    // Reset the timeCached to now
    this.cache.set(cacheKey, {
      ...cached,
      timeCached: Date.now(),
    });
  }

  /**
   * Checks if the cached entry is still within its max-age.
   */
  private isCacheFresh(cached: CachedResponse): boolean {
    if (!cached.maxAge || cached.maxAge <= 0) {
      // No valid max-age directive => treat as stale or handle a default
      return false;
    }
    const age = (Date.now() - cached.timeCached) / 1000; // in seconds
    return age < cached.maxAge;
  }

  /**
   * Parse Cache-Control header directives into a structured object.
   * For example: "no-transform, max-age=10800" => {noTransform: true, maxAge: 10800}
   */
  private parseCacheControlHeader(header: string): CacheDirectives {
    const directives: CacheDirectives = {};

    if (!header) {
      return directives;
    }

    // Split on comma; each part is a directive or directive=value
    const parts = header.split(',').map(part => part.trim());

    parts.forEach(part => {
      // e.g., part="max-age=10800" or part="no-store" or ...
      if (part.includes('=')) {
        const [directive, value] = part.split('=').map(x => x.trim());
        if (directive === 'max-age') {
          const parsed = parseInt(value, 10);
          if (!isNaN(parsed)) {
            directives.maxAge = parsed;
          }
        } else {
          directives[directive] = value;
        }
      } else {
        directives[part] = true;
      }
    });

    return directives;
  }
}
