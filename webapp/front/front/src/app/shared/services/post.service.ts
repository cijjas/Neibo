import { HttpClient, HttpParams } from '@angular/common/http'
import { Post, PostDto, PostForm } from '../models/post'
import { UserDto } from "../models/user"
import { Channel, ChannelDto } from "../models/channel"
import { ImageDto } from "../models/image"
import { TagDto } from "../models/tag"
import { LikeDto } from "../models/like"
import { CommentDto } from "../models/comment"
import { Observable, forkJoin, of, catchError } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'
import { PostStatus } from '../models/postStatus'

@Injectable({providedIn: 'root'})
export class PostService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getPost(neighborhoodId: number, postId: number): Observable<Post> {
        const postDto$ = this.http.get<PostDto>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts/${postId}`);

        return postDto$.pipe(
            mergeMap((postDto: PostDto) => {
                console.log(postDto)
                return forkJoin([
                    this.http.get<UserDto>(postDto.user),
                    this.http.get<ChannelDto>(postDto.channel),
                    //this.http.get<ImageDto>(postDto.postPicture),
                    this.http.get<CommentDto[]>(postDto.comments),
                    this.http.get<TagDto[]>(postDto.tags),
                    this.http.get<LikeDto[]>(postDto.likes),
                    this.http.get<UserDto[]>(postDto.subscribers)
                ]).pipe(
                    map(([user, channel, comments, tags, likes, subscribers]) => {
                      console.log(postId)
                      console.log(postDto.title)
                      console.log(postDto.description)
                      console.log(postDto.date)
                      console.log(user)
                      console.log(channel.channel)
                      console.log(comments)
                      console.log(tags)
                      console.log(likes)
                      console.log(subscribers)
                      console.log(postDto.self)
                        return {
                            postId: postId,
                            title: postDto.title,
                            description: postDto.description,
                            date: postDto.date,
                            user: user,
                            channel: channel.channel,
                            // postPicture: postPicture,
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

    public getPosts(neighborhoodId: number, channel: Channel, tags: string[], postStatus: PostStatus, userId: number, page: number, size: number): Observable<Post[]> {
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
                this.http.get<UserDto>(postDto.user),
                this.http.get<ChannelDto>(postDto.channel),
                //this.http.get<ImageDto>(postDto.postPicture),
                this.http.get<CommentDto[]>(postDto.comments),
                this.http.get<TagDto[]>(postDto.tags),
                this.http.get<LikeDto[]>(postDto.likes),
                this.http.get<UserDto[]>(postDto.subscribers),
              ]).pipe(
                map(([user, channel, comments, tags, likes, subscribers]) => {

                  const postId = this.extractIdFromSelf(postDto.self)

                  return {
                    postId: postId,
                    title: postDto.title,
                    description: postDto.description,
                    date: postDto.date,
                    user: user,
                    channel: channel.channel,
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

    private extractIdFromSelf(selfUrl: string): number {
      // Assuming the postId is the last part of the URL, extract it using a regular expression
      const regex = /\/(\d+)$/;
      const match = selfUrl.match(regex);
    
      // Return the extracted postId or handle it based on your requirements
      return match ? +match[1] : -1; // Convert to number or handle the case where postId extraction fails
    }

    public addPost(neighborhoodId: number, post: PostForm): Observable<PostForm> {
        return this.http.post<PostForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/posts`, post)
    }

}
