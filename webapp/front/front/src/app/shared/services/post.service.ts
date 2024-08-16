import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { Post, PostDto, PostForm } from '../models/post'
import { UserDto } from "../models/user"
import { BaseChannel, BaseChannelDto } from "../models/baseChannel"
import { ImageDto } from "../models/image"
import { TagDto } from "../models/tag"
import { LikeDto } from "../models/like"
import { CommentDto } from "../models/comment"
import { Observable, forkJoin, of, catchError } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'
import { PostStatus } from '../models/postStatus'
import { LoggedInService } from './loggedIn.service'

@Injectable({providedIn: 'root'})
export class PostService {
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

    public getPost(post: string): Observable<Post> {
        const postDto$ = this.http.get<PostDto>(post, { headers: this.headers });

        return postDto$.pipe(
            mergeMap((postDto: PostDto) => {
                return forkJoin([
                    this.http.get<UserDto>(postDto._links.user),
                    this.http.get<BaseChannelDto>(postDto._links.channel),
                    this.http.get<ImageDto>(postDto._links.postPicture),
                    this.http.get<CommentDto[]>(postDto._links.comments),
                    this.http.get<TagDto[]>(postDto._links.tags),
                    this.http.get<LikeDto[]>(postDto._links.likes),
                    this.http.get<UserDto[]>(postDto._links.subscribers)
                ]).pipe(
                    map(([user, channel, postPicture, comments, tags, likes, subscribers]) => {
                        return {
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
                            self: postDto._links.self
                        } as Post;
                    })
                );
            })
        );
    }

    public getPosts(neighborhood: string, channel: string, tags: string[], postStatus: string, user: string, page: number, size: number): Observable<Post[]> {
        let params = new HttpParams();

        if (channel) params = params.set('inChannel', channel);
        if (tags && tags.length > 0) params = params.set('withTags', tags.toString());
        if (postStatus) params = params.set('withStatus', postStatus);
        if (user) params = params.set('postedBy', user);
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<PostDto[]>(`${neighborhood}/posts`, { params, headers: this.headers }).pipe(
          mergeMap((postsDto: PostDto[]) => {

            const postsObservable = postsDto.map((postDto) =>
              forkJoin([
                this.http.get<UserDto>(postDto._links.user),
                this.http.get<BaseChannelDto>(postDto._links.channel),
                //this.http.get<ImageDto>(postDto.postPicture),
                this.http.get<CommentDto[]>(postDto._links.comments),
                this.http.get<TagDto[]>(postDto._links.tags),
                this.http.get<LikeDto[]>(postDto._links.likes),
                this.http.get<UserDto[]>(postDto._links.subscribers),
              ]).pipe(
                map(([user, channel, comments, tags, likes, subscribers]) => {

                  return {
                    title: postDto.title,
                    description: postDto.description,
                    date: postDto.date,
                    user: user,
                    channel: channel,
                    comments: comments,
                    tags: tags,
                    likes: likes,
                    subscribers: subscribers,
                    self: postDto._links.self,
                  } as Post;
                })
              )
            );

            return forkJoin(postsObservable);

          })
        );
    }

    public addPost(neighborhood: string, post: PostForm): Observable<PostForm> {
        return this.http.post<PostForm>(`${neighborhood}/posts`, post, { headers: this.headers })
    }

}
