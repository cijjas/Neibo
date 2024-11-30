
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

    public getPosts(
        postsUrl: string,
        queryParams: {
            page?: number;
            size?: number;
            inChannel?: string;
            withTags?: string[];
            withStatus?: string;
            postedBy?: string;
        } = {}
    ): Observable<Post[]> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.inChannel) params = params.set('inChannel', queryParams.inChannel);
        if (queryParams.withTags !== undefined) params = params.set('withTags', queryParams.withTags.join(','));
        if (queryParams.withStatus) params = params.set('withStatus', queryParams.withStatus);
        if (queryParams.postedBy) params = params.set('postedBy', queryParams.postedBy);

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
                createdAt: postDto.creationDate,
                image: postDto._links.postImage,
                channel: channelDto.name,
                likeCount: likeCountDto.count,
                author: user,
                self: postDto._links.self
            } as Post;
        })
    );
}
