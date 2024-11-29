import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Comment } from '../../models/index';
import { CommentDto, UserDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';

@Injectable({ providedIn: 'root' })
export class CommentService {
    constructor(private http: HttpClient) { }

    public getComment(url: string): Observable<Comment> {
        return this.http.get<CommentDto>(url).pipe(
            mergeMap((commentDto: CommentDto) => mapComment(this.http, commentDto))
        );
    }

    public getComments(url: string, page: number, size: number): Observable<Comment[]> {
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
        http.get<UserDto>(commentDto._links.commentUser).pipe(mergeMap(userDto => mapUser(http, userDto)))
    ]).pipe(
        map(([author]) => {
            return {
                comment: commentDto.message,
                date: commentDto.creationDate,
                author: author,
                self: commentDto._links.self
            } as Comment;
        })
    );
}
