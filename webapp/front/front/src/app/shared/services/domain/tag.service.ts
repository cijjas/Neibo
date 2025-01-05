import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { parseLinkHeader, Tag, TagDto } from '@shared/index';
import { HateoasLinksService } from '@core/index';

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
    ): Observable<{ tags: Tag[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.onPost) params = params.set('onPost', queryParams.onPost);

        return this.http.get<TagDto[]>(url, { params, observe: 'response' }).pipe(
            mergeMap((response: HttpResponse<TagDto[]>) => {
                // Handle 204 No Content response
                if (response.status === 204 || !response.body) {
                    return of({
                        tags: [],
                        totalPages: 0,
                        currentPage: 0,
                    });
                }

                // Parse the response body
                const tagsDto = response.body || [];
                const tags = tagsDto.map(mapTag);

                // Extract pagination info from the 'Link' header if available
                const linkHeader = response.headers.get('Link');
                const paginationInfo = linkHeader ? parseLinkHeader(linkHeader) : { totalPages: 0, currentPage: 0 };

                return of({
                    tags,
                    totalPages: paginationInfo.totalPages || 0,
                    currentPage: paginationInfo.currentPage || 0,
                });
            }),
            catchError((error) => {
                console.error('Error fetching tags:', error);
                return of({ tags: [], totalPages: 0, currentPage: 0 });
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