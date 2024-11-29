
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

    public listPosts(postsUrl: string, page: number, size: number): Observable<Post[]> {
        let params = new HttpParams();
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
        http.get<ImageDto>(postDto._links.postImage),
        http.get<ChannelDto>(postDto._links.channel),
        http.get<LikeCountDto>(postDto._links.likeCount),
        http.get<UserDto>(postDto._links.postUser).pipe(mergeMap(userDto => mapUser(http, userDto)))
    ]).pipe(
        map(([imageDto, channelDto, likeCountDto, user]) => {
            return {
                title: postDto.title,
                body: postDto.body,
                date: postDto.creationDate,
                postImage: imageDto.data,
                channel: channelDto.name,
                likeCount: likeCountDto.likeCount,
                authorUser: user,
                self: postDto._links.self
            } as Post;
        })
    );
}
