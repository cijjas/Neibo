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
  constructor(
    private tokenService: TokenService,
    private authService: AuthService
  ) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    // Attach access token if available
    const token = this.tokenService.getAccessToken();

    let authReq = request;
    if (token) {
      authReq = request.clone({
        setHeaders: { Authorization: `Bearer ${token}` },
      });
    }

    return next.handle(authReq).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          console.log('Got 401, attempting refresh...');

          return this.authService.refreshToken().pipe(
            switchMap((success) => {
              if (!success) {
                this.authService.logout();
                return throwError(() => error);
              }
              const newToken = this.tokenService.getAccessToken();
              const newAuthReq = request.clone({
                setHeaders: {
                  Authorization: `Bearer ${newToken}`,
                },
              });
              return next.handle(newAuthReq);
            })
          );
        }
        return throwError(() => error);
      })
    );
  }
}
