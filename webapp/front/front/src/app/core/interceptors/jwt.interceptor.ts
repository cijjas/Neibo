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
import { Observable, throwError, BehaviorSubject, of } from 'rxjs';
import { catchError, switchMap, filter, take, finalize } from 'rxjs/operators';

import { TokenService } from '@core/services/token.service';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { AuthService } from '@core/services/auth.service';

export const SKIP_ACCESS_TOKEN = new HttpContextToken<boolean>(() => false);

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);

  private readonly apiServerUrl = environment.apiBaseUrl;

  constructor(
    private tokenService: TokenService,
    private http: HttpClient,
    private authService: AuthService
  ) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const skip = request.context.get(SKIP_ACCESS_TOKEN);
    if (!skip) {
      const accessToken = this.tokenService.getAccessToken();
      if (accessToken) {
        request = this.addTokenHeader(request, accessToken);
      }
    }

    return next.handle(request).pipe(
      catchError((error) => {
        if (
          error instanceof HttpErrorResponse &&
          error.status === 401 &&
          !skip
        ) {
          console.warn(`401 error intercepted for URL: ${request.url}`);
          return this.handle401Error(request, next);
        }

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
      this.tokenService.setRefreshingTokenState(true); // Notify refresh started
      this.refreshTokenSubject.next(null);

      return this.refreshToken().pipe(
        switchMap((newAccessToken: string) => {
          this.tokenService.setAccessToken(newAccessToken);
          this.refreshTokenSubject.next(newAccessToken);

          // Retry the original request with the new access token
          const clonedRequest = this.addTokenHeader(request, newAccessToken);
          return next.handle(clonedRequest);
        }),
        catchError((refreshError) => {
          console.error('Failed to refresh token:', refreshError);
          this.authService.logout(); // Log out the user if refresh fails
          return throwError(() => refreshError);
        }),
        finalize(() => {
          this.isRefreshing = false;
          this.tokenService.setRefreshingTokenState(false); // Notify refresh ended
        })
      );
    } else {
      // Wait for the token to refresh before proceeding with the request
      return this.refreshTokenSubject.pipe(
        filter((token) => token !== null),
        take(1),
        switchMap((token) => {
          const clonedRequest = this.addTokenHeader(request, token as string);
          return next.handle(clonedRequest);
        })
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
            const newAccessToken = error.headers.get('X-Access-Token');
            if (newAccessToken) {
              return of(newAccessToken); // Emit the new access token
            } else {
              console.error('No access token found in response headers.');
              this.authService.logout();
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
