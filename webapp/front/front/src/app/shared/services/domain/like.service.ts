import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Like } from '../../models/index';
import { LikeDto, PostDto } from '../../dtos/app-dtos';
import { mapPost } from './post.service';

@Injectable({ providedIn: 'root' })
export class LikeService {
    constructor(private http: HttpClient) { }

    public getLike(url: string): Observable<Like> {
        return this.http.get<LikeDto>(url).pipe(
            mergeMap((likeDto: LikeDto) => mapLike(this.http, likeDto))
        );
    }

    public listLikes(url: string, page: number, size: number): Observable<Like[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<LikeDto[]>(url, { params }).pipe(
            mergeMap((likesDto: LikeDto[]) => {
                const likeObservables = likesDto.map((likeDto) =>
                    mapLike(this.http, likeDto)
                );
                return forkJoin(likeObservables);
            })
        );
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

