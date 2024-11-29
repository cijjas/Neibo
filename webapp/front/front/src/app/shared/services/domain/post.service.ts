
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Post } from '../../models/index';
import { ChannelDto, ImageDto, PostDto, UserDto, LikeCountDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';



@Injectable({ providedIn: 'root' })
export class PostService {

    constructor(
        private http: HttpClient
    ) { }

    public getPost(postUrl: string): Observable<Post> {
        return this.http.get<PostDto>(postUrl).pipe(
            mergeMap((postDto: PostDto) => mapPost(this.http, postDto))
        );
    }

    public getPosts(postsUrl: string, page: number, size: number): Observable<Post[]> {
        let params = new HttpParams();
        // QP inChannel=channelUrl
        // QP withTags=tagUrlList (ie ["http://localhost:8080/neighborhoods/1/tags/2", ["http://localhost:8080/neighborhoods/1/tags/3"]])
        // QP withStatus=postStatusUrl
        // QP postedBy=userUrl
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<PostDto[]>(postsUrl, { params }).pipe(
            mergeMap((postsDto: PostDto[]) => {
                const postObservables = postsDto.map(postDto => mapPost(this.http, postDto));
                return forkJoin(postObservables);
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
                date: postDto.creationDate,
                postImage: postDto._links.postImage,
                channel: channelDto.name,
                likeCount: likeCountDto.count,
                author: user,
                self: postDto._links.self
            } as Post;
        })
    );
}
