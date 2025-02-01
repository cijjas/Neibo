import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { HateoasLinksService } from '@core/index';
import { LinkKey } from '@shared/index';
import { environment } from 'environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  private fallbackImage = environment.deployUrl + 'assets/images/default-profile.png';

  constructor(
    private http: HttpClient,
    private sanitizer: DomSanitizer,
    private linkService: HateoasLinksService
  ) {}

  fetchImage(
    url: string | undefined | null
  ): Observable<{ safeUrl: SafeUrl; isFallback: boolean }> {
    if (!url) {
      return of({
        safeUrl: this.sanitizer.bypassSecurityTrustUrl(this.fallbackImage),
        isFallback: true,
      });
    }
    return this.http.get(url, { responseType: 'blob' }).pipe(
      map((blob) => {
        const reader = new FileReader();
        const readerObservable = new Observable<{
          safeUrl: SafeUrl;
          isFallback: boolean;
        }>((observer) => {
          reader.onloadend = () => {
            const dataUrl = reader.result as string;
            observer.next({
              safeUrl: this.sanitizer.bypassSecurityTrustUrl(dataUrl),
              isFallback: false,
            });
            observer.complete();
          };
          reader.onerror = () => {
            observer.next({
              safeUrl: this.sanitizer.bypassSecurityTrustUrl(
                this.fallbackImage
              ),
              isFallback: true,
            });
            observer.complete();
          };
          reader.readAsDataURL(blob);
        });
        return readerObservable;
      }),
      // Flatten the nested Observable
      switchMap((innerObservable) => innerObservable),
      catchError(() =>
        of({
          safeUrl: this.sanitizer.bypassSecurityTrustUrl(this.fallbackImage),
          isFallback: true,
        })
      )
    );
  }

  createImage(image: File): Observable<string> {
    const uploadUrl = this.linkService.getLink(LinkKey.IMAGES);

    const formData: FormData = new FormData();
    formData.append('imageFile', image, image.name);

    return this.http.post(uploadUrl, formData, { observe: 'response' }).pipe(
      map((response) => {
        const locationHeader = response.headers.get('Location');
        if (locationHeader) {
          return locationHeader;
        } else {
          throw new Error('Location header not found in the response');
        }
      })
    );
  }
}
