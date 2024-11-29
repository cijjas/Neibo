import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Tag } from '../model/tag';
import { TagDto } from '../dtos/app-dtos';

@Injectable({ providedIn: 'root' })
export class TagService {
    constructor(private http: HttpClient) {}

    public getTag(url: string): Observable<Tag> {
        return this.http.get<TagDto>(url).pipe(
            map((tagDto: TagDto) => mapTag(tagDto))
        );
    }

    public listTags(url: string, page: number, size: number): Observable<Tag[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<TagDto[]>(url, { params }).pipe(
            map((tagsDto: TagDto[]) => tagsDto.map(mapTag))
        );
    }
}

export function mapTag(tagDto: TagDto): Tag {
    return {
        name: tagDto.name,
        self: tagDto._links.self
    };
}
