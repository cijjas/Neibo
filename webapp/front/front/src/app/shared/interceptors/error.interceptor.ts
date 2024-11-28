import { Injectable } from '@angular/core';
import {
    HttpEvent,
    HttpInterceptor,
    HttpHandler,
    HttpRequest,
    HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(private router: Router) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req).pipe(
            catchError((error: HttpErrorResponse) => {
                console.error('HTTP Error:', error); // Debugging log
                if (error.status === 404) {
                    this.router.navigate(['/not-found'], {
                        queryParams: {
                            code: '404',
                            message: 'The resource you are looking for was not found.'
                        }
                    });
                } else if (error.status === 500) {
                    this.router.navigate(['/not-found'], {
                        queryParams: {
                            code: '500',
                            message: 'An internal server error occurred. Please try again later.'
                        }
                    });
                }
                return throwError(() => error);
            })
        );
    }
}
