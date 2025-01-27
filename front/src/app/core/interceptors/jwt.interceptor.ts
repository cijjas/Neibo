import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
  HttpContext,
  HttpContextToken,
  HttpResponse,
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take, finalize } from 'rxjs/operators';

import { TokenService } from '@core/services/token.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export const SKIP_ACCESS_TOKEN = new HttpContextToken<boolean>(() => false);

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);

  private readonly apiServerUrl = environment.apiBaseUrl;

  constructor(private tokenService: TokenService, private http: HttpClient) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    console.log('Intercepting request...');

    const skip = request.context.get(SKIP_ACCESS_TOKEN);
    if (!skip) {
      const accessToken = this.tokenService.getAccessToken();
      if (accessToken) {
        request = this.addTokenHeader(request, accessToken);
      }
    }

    return next.handle(request).pipe(
      catchError((error) => {
        // Handle 401 errors
        if (
          error instanceof HttpErrorResponse &&
          error.status === 401 &&
          !skip
        ) {
          // We got a 401 with a normal request -> possible token expiry
          return this.handle401Error(request, next);
        }

        // If it's not a 401 error (or we explicitly skip the token), just propagate
        return throwError(() => error);
      })
    );
  }

  private handle401Error(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      return this.refreshToken().pipe(
        switchMap((newAccessToken: string) => {
          console.log('New access token received:', newAccessToken);

          // Update the token storage with the new access token
          this.tokenService.setAccessToken(newAccessToken);
          this.refreshTokenSubject.next(newAccessToken);

          // Retry the original request with the new token
          return next.handle(this.addTokenHeader(request, newAccessToken));
        }),
        catchError((refreshError) => {
          console.error('Failed to refresh token:', refreshError);
          this.tokenService.clearTokens();
          this.refreshTokenSubject.error(refreshError);
          return throwError(() => refreshError);
        }),
        finalize(() => {
          this.isRefreshing = false;
        })
      );
    } else {
      // If a refresh is already in progress, queue the request
      return this.refreshTokenSubject.pipe(
        filter((token) => token !== null),
        take(1),
        switchMap((token) =>
          next.handle(this.addTokenHeader(request, token as string))
        )
      );
    }
  }

  /**
   * Refresh the access token using the refresh token.
   * Expects a 401 response with a new access token in the `X-Access-Token` header.
   */
  private refreshToken(): Observable<string | HttpResponse<Object>> {
    const refreshToken = this.tokenService.getRefreshToken();

    if (!refreshToken) {
      console.error('No refresh token available.');
      return throwError(() => new Error('No refresh token stored'));
    }

    return this.http
      .get(`${this.apiServerUrl}/`, {
        observe: 'response',
        headers: {
          Authorization: `Bearer ${refreshToken}`,
        },
        context: new HttpContext().set(SKIP_ACCESS_TOKEN, true),
      })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 401) {
            console.log(
              '401 received for refresh token request. Checking for new token...'
            );
            const newAccessToken = error.headers.get('X-Access-Token');
            if (newAccessToken) {
              console.log(
                'New access token found in 401 response header:',
                newAccessToken
              );
              return [newAccessToken]; // Resolve with the new access token
            } else {
              console.error('No access token found in response headers.');
            }
          }

          return throwError(() => new Error('Token refresh failed'));
        })
      );
  }

  /**
   * Helper to clone the request and set the Authorization header
   */
  private addTokenHeader(
    request: HttpRequest<any>,
    token: string
  ): HttpRequest<any> {
    return request.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
  }
}
