import { HttpClient, HttpParams } from '@angular/common/http'
import { Image, ImageDto, ImageForm } from './image'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class ImageService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getImage(imageId : number): Observable<Image> {
        return this.http.get<ImageForm>(`${this.apiServerUrl}/images/${imageId}`)
    }

    public addImage(image: ImageForm): Observable<ImageForm> {
        return this.http.post<ImageForm>(`${this.apiServerUrl}/images`, image)
    }

}
