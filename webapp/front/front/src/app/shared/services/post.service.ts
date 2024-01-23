import { HttpClient, HttpParams } from '@angular/common/http'
import { Post, PostDto, PostForm } from '../models/post'
import { UserDto } from "../models/user"
import { Channel } from "../models/channel"
import { ImageDto } from "../models/image"
import { TagDto } from "../models/tag"
import { LikeDto } from "../models/like"
import { CommentDto } from "../models/comment"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class PostService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getPost(neighborhoodId: number, postId: number): Observable<Post> {
        const postDto$ = this.http.get<PostDto>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts/${postId}`);

        return postDto$.pipe(
            mergeMap((postDto: PostDto) => {
                return forkJoin([
                    this.http.get<UserDto>(postDto.user),
                    this.http.get<Channel>(postDto.channel),
                    this.http.get<ImageDto>(postDto.postPicture),
                    this.http.get<CommentDto[]>(postDto.comments),
                    this.http.get<TagDto[]>(postDto.tags),
                    this.http.get<LikeDto[]>(postDto.likes),
                    this.http.get<UserDto[]>(postDto.subscribers)
                ]).pipe(
                    map(([user, channel, postPicture, comments, tags, likes, subscribers]) => {
                        return {
                            postId: postDto.postId,
                            title: postDto.title,
                            description: postDto.description,
                            date: postDto.date,
                            user: user,
                            channel: channel,
                            postPicture: postPicture,
                            comments: comments,
                            tags: tags,
                            likes: likes,
                            subscribers: subscribers,
                            self: postDto.self
                        } as Post;
                    })
                );
            })
        );
    }

    public getPosts(neighborhoodId: number, channel: string, tags: string[], postStatus: string, userId: number, page: number, size: number): Observable<Post[]> {
        const params = new HttpParams().set('channel', channel).set('tags', tags.toString()).set('postStatus', postStatus).set('user', userId.toString()).set('page', page.toString()).set('size', size.toString())

        return this.http.get<PostDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts`, { params }).pipe(
            mergeMap((postsDto: PostDto[]) => {
                const postsObservable = postsDto.map(postDto =>
                    forkJoin([
                        this.http.get<UserDto>(postDto.user),
                        this.http.get<Channel>(postDto.channel),
                        this.http.get<ImageDto>(postDto.postPicture),
                        this.http.get<CommentDto[]>(postDto.comments),
                        this.http.get<TagDto[]>(postDto.tags),
                        this.http.get<LikeDto[]>(postDto.likes),
                        this.http.get<UserDto[]>(postDto.subscribers)
                    ]).pipe(
                        map(([user, channel, postPicture, comments, tags, likes, subscribers]) => {
                            return {
                                postId: postDto.postId,
                                title: postDto.title,
                                description: postDto.description,
                                date: postDto.date,
                                user: user,
                                channel: channel,
                                postPicture: postPicture,
                                comments: comments,
                                tags: tags,
                                likes: likes,
                                subscribers: subscribers,
                                self: postDto.self
                            } as Post;
                        })
                    )
                );

                 return forkJoin(postsObservable);
            })
        );
    }

    public addPost(neighborhoodId: number, post: PostForm): Observable<PostForm> {
        return this.http.post<PostForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts`, post)
    }

}
