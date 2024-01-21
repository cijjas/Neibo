import { HttpClient, HttpParams } from '@angular/common/http'
import { Comment, CommentDto, CommentForm } from './comment'
import { UserDto } from './user'
import { PostDto } from './post'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class CommentService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getComments(neighborhoodId : number, postId : number, page : number, size : number): Observable<Comment[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
            
        return this.http.get<CommentDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts/${postId}/comments`, { params }).pipe(
            mergeMap((commentsDto: CommentDto[]) => {
                const commentObservables = commentsDto.map(commentDto => 
                    forkJoin([
                        this.http.get<UserDto>(commentDto.user),
                        this.http.get<PostDto>(commentDto.post)
                    ]).pipe(
                        map(([user, post]) => {
                            return {
                                commentId: commentDto.commentId,
                                comment: commentDto.comment,
                                date: commentDto.date,
                                user: user,
                                post: post,
                                self: commentDto.self
                            } as Comment;
                        })
                    )
                );
        
                 return forkJoin(commentObservables);
            })
        );
    }

    public addComment(comment: CommentForm, neighborhoodId : number, postId : number): Observable<CommentForm> {
        return this.http.post<CommentForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts/${postId}/comments`, comment)
    }

}
