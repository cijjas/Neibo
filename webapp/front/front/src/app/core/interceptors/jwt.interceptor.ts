// jwt.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import { catchError, Observable, switchMap, throwError } from 'rxjs';
import { AuthService } from '@core/index';
import { TokenService } from '@core/services/token.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  private isRefreshing = false; // Track the refresh token state

  constructor(
    private tokenService: TokenService,
    private authService: AuthService
  ) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const token = this.tokenService.getAccessToken();

    let authReq = request;
    if (token) {
      authReq = request.clone({
        setHeaders: { Authorization: `Bearer ${token}` },
      });
    }

    return next.handle(authReq).pipe(
      catchError((error) => {
        if (
          error instanceof HttpErrorResponse &&
          error.status === 401 &&
          !this.isRefreshing &&
          !request.headers.has('X-Retry')
        ) {
          this.isRefreshing = true; // Prevent multiple refresh attempts
          console.log('Got 401, attempting refresh...');

          return this.authService.refreshToken().pipe(
            switchMap((success) => {
              this.isRefreshing = false; // Reset the flag
              if (!success) {
                this.authService.logout();
                return throwError(() => error);
              }

              const newToken = this.tokenService.getAccessToken();
              const newAuthReq = request.clone({
                setHeaders: {
                  Authorization: `Bearer ${newToken}`,
                  'X-Retry': 'true', // Add custom header to prevent infinite retries
                },
              });
              return next.handle(newAuthReq);
            }),
            catchError((refreshError) => {
              this.isRefreshing = false; // Reset the flag on failure
              this.authService.logout();
              return throwError(() => refreshError);
            })
          );
        }
        return throwError(() => error);
      })
    );
  }
}
