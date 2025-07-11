import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private router: Router) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.error instanceof ErrorEvent) {
          // Client-side or network error
          console.error('Client-side error:', error.error.message);
          console.log('Network error: Please check your connection.');
        } else {
          // Backend error
          switch (error.status) {
            // case 403:
            case 404:
            case 503:
            case 500:
              this.router.navigate(['/not-found'], {
                queryParams: {
                  code: error.status,
                  message: error.message,
                },
              });
              break;

            case 0:
              this.router.navigate(['/not-found'], {
                queryParams: {
                  code: error.status,
                  message:
                    'It seems like you are offline. Check your internet connection.',
                },
              });
              break;

            default:
              break;
          }
        }

        return throwError(() => error);
      }),
    );
  }
}
