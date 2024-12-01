import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Comment } from '../../models/index';
import { CommentDto, UserDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';
import { formatDistanceToNow } from 'date-fns';
import { parseLinkHeader } from './utils';

@Injectable({ providedIn: 'root' })
export class CommentService {
    constructor(private http: HttpClient) { }

    public getComment(url: string): Observable<Comment> {
        return this.http.get<CommentDto>(url).pipe(
            mergeMap((commentDto: CommentDto) => mapComment(this.http, commentDto))
        );
    }

    public getComments(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
        } = {}
    ): Observable<{ comments: Comment[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http
            .get<CommentDto[]>(url, { params, observe: 'response' })
            .pipe(
                mergeMap((response: HttpResponse<CommentDto[]>) => {
                    const commentsDto = response.body || [];
                    const linkHeader = response.headers.get('Link');
                    const paginationInfo = parseLinkHeader(linkHeader);

                    const commentObservables = commentsDto.map((commentDto) =>
                        mapComment(this.http, commentDto)
                    );

                    return forkJoin(commentObservables).pipe(
                        map((comments) => ({
                            comments,
                            totalPages: paginationInfo.totalPages,
                            currentPage: paginationInfo.currentPage,
                        }))
                    );
                })
            );
    }
}

export function mapComment(http: HttpClient, commentDto: CommentDto): Observable<Comment> {
    return forkJoin([
        http
            .get<UserDto>(commentDto._links.commentUser)
            .pipe(mergeMap((userDto) => mapUser(http, userDto))),
    ]).pipe(
        map(([author]) => {
            return {
                message: commentDto.message,
                createdAt: commentDto.creationDate,
                user: author,
                humanReadableDate: formatDistanceToNow(new Date(commentDto.creationDate), {
                    addSuffix: true,
                }),
                self: commentDto._links.self,
            } as Comment;
        })
    );
}
