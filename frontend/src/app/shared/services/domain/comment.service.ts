import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import {
  Comment,
  CommentDto,
  UserDto,
  mapUser,
  parseLinkHeader,
} from '@shared/index';
import { formatDistanceToNow } from 'date-fns';
import { enUS, es } from 'date-fns/locale';

@Injectable({ providedIn: 'root' })
export class CommentService {
  constructor(private http: HttpClient) {}

  public getComment(url: string): Observable<Comment> {
    return this.http
      .get<CommentDto>(url)
      .pipe(
        mergeMap((commentDto: CommentDto) => mapComment(this.http, commentDto)),
      );
  }
  public getComments(
    url: string,
    queryParams: {
      page?: number;
      size?: number;
    } = {},
  ): Observable<{
    comments: Comment[];
    totalPages: number;
    currentPage: number;
  }> {
    let params = new HttpParams();

    if (queryParams.page !== undefined) {
      params = params.set('page', queryParams.page.toString());
    }
    if (queryParams.size !== undefined) {
      params = params.set('size', queryParams.size.toString());
    }

    return this.http
      .get<CommentDto[]>(url, { params, observe: 'response' })
      .pipe(
        mergeMap((response: HttpResponse<CommentDto[]>) => {
          const commentsDto = response.body || [];
          const linkHeader = response.headers.get('Link');
          const paginationInfo = parseLinkHeader(linkHeader);
          const commentObservables = commentsDto.map(commentDto =>
            mapComment(this.http, commentDto),
          );

          // If no comments, return an observable with an empty list.
          if (commentObservables.length === 0) {
            return of({
              comments: [],
              totalPages: paginationInfo.totalPages,
              currentPage: paginationInfo.currentPage,
            });
          }

          // Otherwise, forkJoin the comment observables.
          return forkJoin(commentObservables).pipe(
            map(comments => ({
              comments,
              totalPages: paginationInfo.totalPages,
              currentPage: paginationInfo.currentPage,
            })),
          );
        }),
      );
  }

  public createComment(
    url: string,
    comment: string,
    userUrl: string,
  ): Observable<Comment> {
    const body = {
      message: comment,
      user: userUrl,
    };

    return this.http.post<Comment>(url, body);
  }
}

export function mapComment(
  http: HttpClient,
  commentDto: CommentDto,
): Observable<Comment> {
  return forkJoin([
    http
      .get<UserDto>(commentDto._links.commentUser)
      .pipe(mergeMap(userDto => mapUser(http, userDto))),
  ]).pipe(
    map(([author]) => {
      const locale = localStorage.getItem('language') == 'es' ? es : enUS;
      return {
        message: commentDto.message,
        createdAt: commentDto.creationDate,
        user: author,
        humanReadableDate: formatDistanceToNow(
          new Date(commentDto.creationDate),
          {
            addSuffix: true,
            locale,
          },
        ),
        self: commentDto._links.self,
      } as Comment;
    }),
  );
}
