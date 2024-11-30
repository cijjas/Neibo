import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Resource } from '../../models/index';
import { ImageDto, ResourceDto } from '../../dtos/app-dtos';

@Injectable({ providedIn: 'root' })
export class ResourceService {
    constructor(private http: HttpClient) { }

    public getResource(url: string): Observable<Resource> {
        return this.http.get<ResourceDto>(url).pipe(
            map((resourceDto: ResourceDto) => mapResource(resourceDto))
        );
    }

    public getResources(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
        } = {}
    ): Observable<Resource[]> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http.get<ResourceDto[]>(url, { params }).pipe(
            map((resourcesDto: ResourceDto[]) => resourcesDto.map(mapResource))
        );
    }

}

export function mapResource(resourceDto: ResourceDto): Resource {
    return {
        title: resourceDto.title,
        description: resourceDto.description,
        image: resourceDto._links.resourceImage,
        self: resourceDto._links.self
    }
}
