import { Injectable } from "@angular/core";
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpErrorResponse,
} from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { Router } from "@angular/router";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private router: Router) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.error instanceof ErrorEvent) {
          // Client-side or network error
          console.error("Client-side error:", error.error.message);
          this.showNotification("Network error: Please check your connection.");
        } else {
          // Backend error
          switch (error.status) {
            case 403: // Forbidden
              this.showNotification(
                "You do not have permission to perform this action."
              );
              break;

            case 404: // Not Found
            case 503: // Service Unavailable
            case 500: // Internal Server Error
              this.router.navigate(["/not-found"], {
                queryParams: {
                  code: error.status,
                  message: error.message,
                },
              });
              break;

            default:
              // Generic error handling
              console.error("Unexpected error:", error);
              this.showNotification(
                "An unexpected error occurred. Please try again."
              );
              break;
          }
        }

        return throwError(() => error);
      })
    );
  }

  private showNotification(message: string): void {
    // Replace this with your notification service or logic
    console.log("Notification:", message);
  }
}
