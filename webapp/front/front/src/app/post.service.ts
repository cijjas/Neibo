import { HttpClient, HttpParams } from '@angular/common/http';
import {Post} from './post'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class PostService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getPosts(neighborhoodId: number, channel: string, tags: string[], postStatus: string, userId: number, page: number, size: number): Observable<Post[]> {
        const params = new HttpParams().set('channel', channel).set('tags', tags.toString()).set('postStatus', postStatus).set('user', userId.toString()).set('page', page.toString()).set('size', size.toString());
    
        return this.http.get<Post[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts`, { params });
    }

    public getPost(neighborhoodId: number, postId: number): Observable<Post> {    
        return this.http.get<Post>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts/${postId}`);
    }

    public addPost(neighborhoodId: number, post: Post): Observable<Post> {
        return this.http.post<Post>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts`, post);
    }

}