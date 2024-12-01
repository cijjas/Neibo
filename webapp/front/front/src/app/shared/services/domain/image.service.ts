import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { HateoasLinksService } from '../index.service';

@Injectable({ providedIn: 'root' })
export class ImageService {

    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService
    ) { }

    private createImage(image: File): Observable<string> {
        const uploadUrl = this.linkService.getLink('neighborhood:images');

        const formData: FormData = new FormData();
        formData.append('imageFile', image, image.name);

        return this.http.post(uploadUrl, formData, { observe: 'response' }).pipe(
            map(response => {
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
