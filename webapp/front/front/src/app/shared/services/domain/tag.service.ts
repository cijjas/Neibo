import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Tag } from '../../models/index';
import { TagDto } from '../../dtos/app-dtos';
import { HateoasLinksService } from '../index.service';

@Injectable({ providedIn: 'root' })
export class TagService {
    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService,
    ) { }

    public getTag(url: string): Observable<Tag> {
        return this.http.get<TagDto>(url).pipe(
            map((tagDto: TagDto) => mapTag(tagDto))
        );
    }

    public getTags(
        url: string,
        queryParams: {
            onPost?: string;
            page?: number;
            size?: number;
        } = {}
    ): Observable<Tag[]> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.onPost) params = params.set('onPost', queryParams.onPost);

        return this.http.get<TagDto[]>(url, { params }).pipe(
            map((tagsDto) => tagsDto?.map(mapTag) || []),
            catchError((err) => {
                console.error('Error fetching tags:', err);
                return of([]);
            })
        );
    }

    public createTag(name: string): Observable<string | null> {
        const tagsUrl = this.linkService.getLink('neighborhood:tags');
        return this.http.post(tagsUrl, { name }, { observe: 'response' }).pipe(
            map(response => {
                const locationHeader = response.headers.get('Location');
                if (locationHeader) {
                    return locationHeader;
                } else {
                    console.error('Location header not found');
                    return null;
                }
            }),
            catchError(error => {
                console.error('Error creating tag:', error);
                return of(null);
            })
        );
    }

}

export function mapTag(tagDto: TagDto): Tag {
    return {
        name: tagDto.name,
        self: tagDto._links.self
    };
}