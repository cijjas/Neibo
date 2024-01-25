import { HttpClient, HttpParams } from '@angular/common/http'
import { Post, PostDto, PostForm } from '../models/post'
import { UserDto } from "../models/user"
import { Channel } from "../models/channel"
import { ImageDto } from "../models/image"
import { TagDto } from "../models/tag"
import { LikeDto } from "../models/like"
import { CommentDto } from "../models/comment"
import { Observable, forkJoin, of, catchError } from 'rxjs'
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
        let params = new HttpParams();

        if (channel) params = params.set('channel', channel);
        if (tags && tags.length > 0) params = params.set('tags', tags.join(','));
        if (postStatus) params = params.set('postStatus', postStatus);
        if (userId) params = params.set('user', userId.toString());
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());
    
        return this.http.get<PostDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts`, { params }).pipe(
          mergeMap((postsDto: PostDto[]) => {
            
            const postsObservable = postsDto.map((postDto) =>
              forkJoin([
                this.getRequestOrNull(this.http.get<UserDto>(postDto.user)),
                this.getRequestOrNull(this.http.get<Channel>(postDto.channel)),
                this.getRequestOrNull(this.http.get<ImageDto>(postDto.postPicture)),
                this.getRequestOrNull(this.http.get<CommentDto[]>(postDto.comments)),
                this.getRequestOrNull(this.http.get<TagDto[]>(postDto.tags)),
                this.getRequestOrNull(this.http.get<LikeDto[]>(postDto.likes)),
                this.getRequestOrNull(this.http.get<UserDto[]>(postDto.subscribers)),
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
                    self: postDto.self,
                  } as Post;
                })
              )
            );

            return forkJoin(postsObservable);
            
          })
        );
    }

    private getRequestOrNull<T>(request: Observable<T>): Observable<T | null> {
        console.log('I');
        return request.pipe(
            catchError((error) => {
                console
                console.error('Error fetching data:', error);
                return of(null); 
            })
        );
      }
      

    public addPost(neighborhoodId: number, post: PostForm): Observable<PostForm> {
        return this.http.post<PostForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts`, post)
    }

}
