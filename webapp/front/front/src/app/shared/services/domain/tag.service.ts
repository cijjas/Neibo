import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Tag } from '../../models/index';
import { TagDto } from '../../dtos/app-dtos';

@Injectable({ providedIn: 'root' })
export class TagService {
    constructor(private http: HttpClient) { }

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
}

export function mapTag(tagDto: TagDto): Tag {
    return {
        name: tagDto.name,
        self: tagDto._links.self
    };
}