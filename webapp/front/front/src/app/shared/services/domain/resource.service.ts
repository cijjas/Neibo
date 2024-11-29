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
            mergeMap((resourceDto: ResourceDto) => mapResource(this.http, resourceDto))
        );
    }

    public getResources(url: string, page: number, size: number): Observable<Resource[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<ResourceDto[]>(url, { params }).pipe(
            mergeMap((resourcesDto: ResourceDto[]) => {
                const resourceObservables = resourcesDto.map(resourceDto =>
                    mapResource(this.http, resourceDto)
                );
                return forkJoin(resourceObservables);
            })
        );
    }
}

export function mapResource(http: HttpClient, resourceDto: ResourceDto): Observable<Resource> {
    return forkJoin([
        http.get<ImageDto>(resourceDto._links.resourceImage)
    ]).pipe(
        map(([image]) => {
            return {
                title: resourceDto.title,
                description: resourceDto.description,
                image: image.data,
                self: resourceDto._links.self
            } as Resource;
        })
    );
}
