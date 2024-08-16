import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Tag, TagDto } from '../models/tag'
import { PostDto } from '../models/post'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class TagService {
    private apiServerUrl = environment.apiBaseUrl
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService,
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }

    public getTags(neighborhood: string, post: string, page: number, size: number): Observable<Tag[]> {
        let params = new HttpParams()
        
        if(post) params = params.set('postId', post)
        if(page) params = params.set('page', page.toString())
        if(size) params = params.set('size', size.toString())

        return this.http.get<TagDto[]>(`${neighborhood}/tags`, { params, headers: this.headers }).pipe(
            mergeMap((tagsDto: TagDto[]) => {
                const tagObservables = tagsDto.map(tagDto =>
                    this.http.get<PostDto[]>(tagDto._links.posts).pipe(
                        map((posts) => {
                            return {
                                tag: tagDto.tag,
                                posts: posts,
                                self: tagDto._links.self
                            } as Tag;
                        })
                    )
                );

                 return forkJoin(tagObservables);
            })
        );
    }

}
