
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Post } from '../model/post';
import { ChannelDto, ImageDto, PostDto, UserDto } from '../dtos/app-dtos';
import { LikeCountDto } from '../models/likeCount';
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
        http.get<ImageDto>(postDto._links.image),
        http.get<ChannelDto>(postDto._links.channel),
        http.get<LikeCountDto>(postDto._links.likeCount),
        http.get<UserDto>(postDto._links.user).pipe(mergeMap(userDto => mapUser(http, userDto)))
    ]).pipe(
        map(([imageDto, channelDto, likeCountDto, user]) => {
            return {
                title: postDto.title,
                description: postDto.description,
                date: postDto.date,
                postImage: imageDto.image,
                channel: channelDto.name,
                likeCount: likeCountDto.likeCount,
                authorUser: user,
                self: postDto._links.self
            } as Post;
        })
    );
}
