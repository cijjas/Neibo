import { HttpClient, HttpParams } from '@angular/common/http'
import { Tag, TagDto } from './tag'
import { PostDto } from './post'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class TagService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getTags(neighborhoodId: number, postId: number, page: number, size: number): Observable<Tag[]> {
        const params = new HttpParams().set('postId', postId.toString()).set('page', page.toString()).set('size', size.toString())

        return this.http.get<TagDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/tags`, { params }).pipe(
            mergeMap((tagsDto: TagDto[]) => {
                const tagObservables = tagsDto.map(tagDto => 
                    this.http.get<PostDto[]>(tagDto.posts).pipe(
                        map((posts) => {
                            return {
                                tagId: tagDto.tagId,
                                tag: tagDto.tag,
                                posts: posts,
                                self: tagDto.self
                            } as Tag;
                        })
                    )
                );
        
                 return forkJoin(tagObservables);
            })
        );
    }

}
