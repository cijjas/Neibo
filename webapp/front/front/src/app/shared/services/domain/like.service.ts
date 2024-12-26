import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, throwError } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { Like } from '../../models/index';
import { LikeDto, PostDto } from '../../dtos/app-dtos';
import { mapPost } from './post.service';
import { parseLinkHeader } from './utils';
import { HateoasLinksService } from '../index.service';

@Injectable({ providedIn: 'root' })
export class LikeService {
    constructor(
        private http: HttpClient,
        private linksService: HateoasLinksService
    ) { }

    public getLike(url: string): Observable<Like> {
        return this.http.get<LikeDto>(url).pipe(
            mergeMap((likeDto: LikeDto) => mapLike(this.http, likeDto))
        );
    }

    public getLikes(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
            onPost?: string;
            likedBy?: string;
        } = {}
    ): Observable<{ likes: Like[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.onPost) params = params.set('onPost', queryParams.onPost);
        if (queryParams.likedBy) params = params.set('likedBy', queryParams.likedBy);

        return this.http.get<LikeDto[]>(url, { params, observe: 'response' }).pipe(
            mergeMap((response) => {
                const likesDto: LikeDto[] = response.body || [];
                const pagination = parseLinkHeader(response.headers.get('Link'));

                const likeObservables = likesDto.map(likeDto => mapLike(this.http, likeDto));
                return forkJoin(likeObservables).pipe(
                    map((likes) => {
                        return {
                            likes,
                            totalPages: pagination.totalPages,
                            currentPage: pagination.currentPage
                        };
                    })
                );
            })
        );
    }

    public createLike(likeDto: LikeDto): Observable<string> {
        const likesUrl = this.linksService.getLink('neighborhood:likes');

        return this.http.post(likesUrl, likeDto, { observe: 'response' }).pipe(
            map(response => {
                const locationHeader = response.headers.get('Location');
                if (!locationHeader) {
                    throw new Error('Location header not found in response.');
                }
                return locationHeader;
            }),
            catchError(error => {
                console.error('Error creating like:', error);
                return throwError(() => new Error('Failed to create like.'));
            })
        );
    }


    public deleteLike(onPost: string, likedBy: string): Observable<void> {
        const likesUrl = this.linksService.getLink('neighborhood:likes'); // Base URL for the likes resource

        // Set up the query parameters for the DELETE request
        const params = new HttpParams()
            .set('onPost', onPost)
            .set('likedBy', likedBy);

        // Send the DELETE request
        return this.http.delete<void>(likesUrl, { params });
    }
}

export function mapLike(http: HttpClient, likeDto: LikeDto): Observable<Like> {
    return forkJoin([
        http.get<PostDto>(likeDto._links.post).pipe(mergeMap(postDto => mapPost(http, postDto)))
    ]).pipe(
        map(([post]) => {
            return {
                date: likeDto.likeDate,
                post: post,
                self: likeDto._links.self
            } as Like;
        })
    );
}