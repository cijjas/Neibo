import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http'
import { Comment, CommentDto, CommentForm } from '../models/comment'
import { UserDto } from '../models/user'
import { PostDto } from '../models/post'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'
import { LoggedInService } from './loggedIn.service'

@Injectable({providedIn: 'root'})
export class CommentService {
    private apiServerUrl = environment.apiBaseUrl
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
          })
    }

    public getComments(post: string, page : number, size : number): Observable<Comment[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());

        return this.http.get<CommentDto[]>(`${post}/comments`, { params, headers: this.headers }).pipe(
            mergeMap((commentsDto: CommentDto[]) => {
                const commentObservables = commentsDto.map(commentDto =>
                    forkJoin([
                        this.http.get<UserDto>(commentDto._links.user),
                        this.http.get<PostDto>(commentDto._links.post)
                    ]).pipe(
                        map(([user, post]) => {
                            return {
                                comment: commentDto.comment,
                                date: commentDto.date,
                                user: user,
                                post: post,
                                self: commentDto._links.self
                            } as Comment;
                        })
                    )
                );

                 return forkJoin(commentObservables);
            })
        );
    }

    public addComment(comment: CommentForm, post: string): Observable<CommentForm> {
        return this.http.post<CommentForm>(`${post}/comments`, comment, { headers: this.headers })
    }

}
