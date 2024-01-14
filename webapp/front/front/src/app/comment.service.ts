import { HttpClient, HttpParams } from '@angular/common/http';
import { Comment } from './comment'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class CommentService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getComments(neighborhoodId : number, postId : number, page : number, size : number): Observable<Comment[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
        return this.http.get<Comment[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts/${postId}/comments`)
    }

    public addComment(comment: Comment, neighborhoodId : number, postId : number): Observable<Comment> {
        return this.http.post<Comment>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts/${postId}/comments`, comment)
    }

}