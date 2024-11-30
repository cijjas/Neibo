import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class ImageService {
    private fallbackImage = 'assets/images/roundedPlaceholder.png';

    constructor(private http: HttpClient, private sanitizer: DomSanitizer) { }

    fetchImage(url: string): Observable<SafeUrl> {
        return this.http.get(url, { responseType: 'blob' }).pipe(
            map((blob) => {
                const objectURL = URL.createObjectURL(blob);
                return this.sanitizer.bypassSecurityTrustUrl(objectURL);
            }),
            catchError(() => of(this.fallbackImage)) // Use fallback if there's an error
        );
    }
}
