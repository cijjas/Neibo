import { HttpClient, HttpParams } from '@angular/common/http'
import { Tag, TagDto } from './tag'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class TagService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getTags(neighborhoodId: number, postId: number, page: number, size: number): Observable<Tag[]> {
        const params = new HttpParams().set('postId', postId.toString()).set('page', page.toString()).set('size', size.toString())

        return this.http.get<Tag[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/tags`, { params })
    }

}
