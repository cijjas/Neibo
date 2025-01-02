import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { ChannelDto, PostDto, UserDto, LikeCountDto, Post, mapUser, parseLinkHeader } from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { ApiRegistry } from '@core/services/api-registry.service';


@Injectable({ providedIn: 'root' })
export class PostService {

    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService,
        private apiRegistry: ApiRegistry,
    ) { }

    public getPost(postUrl: string): Observable<Post> {
        return this.http.get<PostDto>(postUrl).pipe(
            mergeMap((postDto: PostDto) => mapPost(this.http, postDto))
        );
    }

    public getWorkerPosts(
        queryParams: {
            page?: number;
            size?: number;
            inChannel?: string;
            withTags?: string[];
            withStatus?: string;
            postedBy?: string;
        } = {}
    ): Observable<{ posts: Post[]; totalPages: number; currentPage: number }> {

        // Retrieve the template endpoint
        const listPostsEndpoint = this.apiRegistry.getEndpoint("root:workerPosts");
        if (!listPostsEndpoint) {
            console.error("Worker posts endpoint not found");
            return of({ posts: [], totalPages: 0, currentPage: 0 });
        }

        // Build the URI using the template and query parameters
        const workerPostsUrl = listPostsEndpoint.buildUri({
            ...queryParams,     // Spread the query parameters directly
        });

        return this.http
            .get<PostDto[]>(workerPostsUrl, { observe: 'response' })
            .pipe(
                mergeMap((response) => {
                    // Handle 204 No Content response
                    if (response.status === 204 || !response.body) {
                        return of({
                            posts: [],
                            totalPages: 0,
                            currentPage: 0,
                        });
                    }

                    // Parse the response for normal cases
                    const postsDto = response.body || [];
                    const linkHeader = response.headers.get('Link');
                    const paginationInfo = linkHeader ? parseLinkHeader(linkHeader) : { totalPages: 0, currentPage: 0 };

                    // Process the posts
                    const postObservables = postsDto.map((postDto) => mapPost(this.http, postDto));

                    return forkJoin(postObservables).pipe(
                        map((posts) => ({
                            posts,
                            totalPages: paginationInfo.totalPages || 0,
                            currentPage: paginationInfo.currentPage || 0,
                        }))
                    );
                }),
                catchError((error) => {
                    console.error('Error fetching posts:', error);
                    return of({ posts: [], totalPages: 0, currentPage: 0 });
                })
            );
    }

    public getPosts(
        queryParams: {
            page?: number;
            size?: number;
            inChannel?: string;
            withTags?: string[];
            withStatus?: string;
            postedBy?: string;
        } = {}
    ): Observable<{ posts: Post[]; totalPages: number; currentPage: number }> {

        // Retrieve the template endpoint
        const postsEndpoint = this.apiRegistry.getEndpoint("neighborhood:posts2");
        if (!postsEndpoint) {
            console.error("Posts endpoint not found");
            return of({ posts: [], totalPages: 0, currentPage: 0 });
        }

        // Build the URI using the template and query parameters
        const postsUrl = postsEndpoint.buildUri({
            ...queryParams, // Spread the query parameters directly
        });

        return this.http
            .get<PostDto[]>(postsUrl, { observe: 'response' })
            .pipe(
                mergeMap((response) => {
                    // Handle 204 No Content response
                    if (response.status === 204 || !response.body) {
                        return of({
                            posts: [],
                            totalPages: 0,
                            currentPage: 0,
                        });
                    }

                    // Parse the response for normal cases
                    const postsDto = response.body || [];
                    const linkHeader = response.headers.get('Link');
                    const paginationInfo = linkHeader ? parseLinkHeader(linkHeader) : { totalPages: 0, currentPage: 0 };

                    // Process the posts
                    const postObservables = postsDto.map((postDto) => mapPost(this.http, postDto));

                    return forkJoin(postObservables).pipe(
                        map((posts) => ({
                            posts,
                            totalPages: paginationInfo.totalPages || 0,
                            currentPage: paginationInfo.currentPage || 0,
                        }))
                    );
                }),
                catchError((error) => {
                    console.error('Error fetching posts:', error);
                    return of({ posts: [], totalPages: 0, currentPage: 0 });
                })
            );
    }

    public createPost(postForm: PostDto): Observable<string | null> {
        const createPostUrl = this.linkService.getLink('neighborhood:posts');

        return this.http.post<PostDto>(createPostUrl, postForm, { observe: 'response' }).pipe(
            map(response => {
                const locationHeader = response.headers.get('Location');
                if (locationHeader) {
                    return locationHeader;
                } else {
                    console.error('Location header not found');
                    return null;
                }
            }),
            catchError(error => {
                console.error('Error creating post:', error);
                return of(null);
            })
        );
    }
}

export function mapPost(http: HttpClient, postDto: PostDto): Observable<Post> {
    return forkJoin([
        http.get<ChannelDto>(postDto._links.channel),
        http.get<LikeCountDto>(postDto._links.likeCount),
        http.get<UserDto>(postDto._links.postUser).pipe(mergeMap(userDto => mapUser(http, userDto)))
    ]).pipe(
        map(([channelDto, likeCountDto, user]) => {
            return {
                title: postDto.title,
                body: postDto.body,
                createdAt: postDto.creationDate,
                image: postDto._links.postImage,
                channel: channelDto.name,
                likeCount: likeCountDto.count,
                comments: postDto._links.comments,
                author: user,
                self: postDto._links.self
            } as Post;
        })
    );
}
