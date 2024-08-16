import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Image, ImageDto, ImageForm } from '../models/image'
import { Observable, map } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'

@Injectable({providedIn: 'root'})
export class ImageService {
    private apiServerUrl = environment.apiBaseUrl
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService,
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }

    public getImage(image : string): Observable<Image> {
        const imageDto$ = this.http.get<ImageDto>(image, { headers: this.headers })

        return imageDto$.pipe(
            map((imageDto: ImageDto) => {
                return {
                    image: imageDto.image,
                    self: imageDto._links.self
                } as Image;
            })
        );
    }

    public addImage(image: ImageForm): Observable<ImageForm> {
        return this.http.post<ImageForm>(`${this.apiServerUrl}/images`, image, { headers: this.headers })
    }

}
