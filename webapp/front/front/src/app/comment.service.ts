import { HttpClient, HttpParams } from '@angular/common/http'
import { CommentForm } from './commentForm'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class CommentService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getComments(neighborhoodId : number, postId : number, page : number, size : number): Observable<CommentForm[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString())
        return this.http.get<CommentForm[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts/${postId}/comments`)
    }

    public addComment(comment: CommentForm, neighborhoodId : number, postId : number): Observable<CommentForm> {
        return this.http.post<CommentForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts/${postId}/comments`, comment)
    }

}
