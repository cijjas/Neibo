import { HttpClient, HttpParams } from '@angular/common/http'
import { Like } from './like'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class LikeService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getLikes(postId : number, userId : number, page : number, size : number): Observable<Like[]> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString())
            .set('postId', postId.toString())
            .set('userId', userId.toString())
        return this.http.get<Like[]>(`${this.apiServerUrl}/likes`)
    }

    public addLike(like: Like): Observable<Like> {
        return this.http.post<Like>(`${this.apiServerUrl}/likes`, like)
    }

}