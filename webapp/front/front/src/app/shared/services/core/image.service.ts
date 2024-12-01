import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Observable, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class ImageService {
    private fallbackImage = 'assets/images/roundedPlaceholder.png';

    constructor(private http: HttpClient, private sanitizer: DomSanitizer) { }

    fetchImage(url: string | undefined | null): Observable<SafeUrl> {
        if (!url) {
            return of(this.sanitizer.bypassSecurityTrustUrl(this.fallbackImage));
        }
        return this.http.get(url, { responseType: 'blob' }).pipe(
            switchMap((blob) => {
                const reader = new FileReader();
                return new Observable<SafeUrl>((observer) => {
                    reader.onloadend = () => {
                        const dataUrl = reader.result as string;
                        observer.next(this.sanitizer.bypassSecurityTrustUrl(dataUrl));
                        observer.complete();
                    };
                    reader.onerror = () => {
                        observer.next(this.sanitizer.bypassSecurityTrustUrl(this.fallbackImage));
                        observer.complete();
                    };
                    reader.readAsDataURL(blob);
                });
            }),
            catchError(() => of(this.sanitizer.bypassSecurityTrustUrl(this.fallbackImage))) // Use fallback if there's an error
        );
    }
}
