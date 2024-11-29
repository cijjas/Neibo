import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Comment } from '../model/comment';
import { CommentDto, UserDto } from '../dtos/app-dtos';
import { mapUser } from './user.service';

@Injectable({ providedIn: 'root' })
export class CommentService {
    constructor(private http: HttpClient) {}

    public getComment(url: string): Observable<Comment> {
        return this.http.get<CommentDto>(url).pipe(
            mergeMap((commentDto: CommentDto) => mapComment(this.http, commentDto))
        );
    }

    public listComments(url: string, page: number, size: number): Observable<Comment[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<CommentDto[]>(url, { params }).pipe(
            mergeMap((commentsDto: CommentDto[]) => {
                const commentObservables = commentsDto.map((commentDto) =>
                    mapComment(this.http, commentDto)
                );
                return forkJoin(commentObservables);
            })
        );
    }
}

export function mapComment(http: HttpClient, commentDto: CommentDto): Observable<Comment> {
    return forkJoin([
        http.get<UserDto>(commentDto._links.user).pipe(mergeMap(userDto => mapUser(http, userDto)))
    ]).pipe(
        map(([author]) => {
            return {
                comment: commentDto.comment,
                date: commentDto.date,
                author: author,
                self: commentDto._links.self
            } as Comment;
        })
    );
}
