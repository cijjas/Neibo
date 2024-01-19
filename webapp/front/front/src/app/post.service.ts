import { HttpClient, HttpParams } from '@angular/common/http'
import { PostForm } from './postForm'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class PostService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getPosts(neighborhoodId: number, channel: string, tags: string[], postStatus: string, userId: number, page: number, size: number): Observable<PostForm[]> {
        const params = new HttpParams().set('channel', channel).set('tags', tags.toString()).set('postStatus', postStatus).set('user', userId.toString()).set('page', page.toString()).set('size', size.toString());

        return this.http.get<PostForm[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts`, { params })
    }

    public getPost(neighborhoodId: number, postId: number): Observable<PostForm> {
        return this.http.get<PostForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts/${postId}`)
    }

    public addPost(neighborhoodId: number, post: PostForm): Observable<PostForm> {
        return this.http.post<PostForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts`, post)
    }

}
