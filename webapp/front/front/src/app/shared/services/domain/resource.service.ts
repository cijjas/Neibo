import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Resource, ResourceDto, parseLinkHeader } from '@shared/index';

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
    ): Observable<{ resources: Resource[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http.get<ResourceDto[]>(url, { params, observe: 'response' }).pipe(
            map((response) => {
                const resourcesDto: ResourceDto[] = response.body || [];
                const pagination = parseLinkHeader(response.headers.get('Link'));

                const resources = resourcesDto.map(mapResource);

                return {
                    resources,
                    totalPages: pagination.totalPages,
                    currentPage: pagination.currentPage,
                };
            })
        );
    }
}

export function mapResource(resourceDto: ResourceDto): Resource {
    return {
        title: resourceDto.title,
        description: resourceDto.description,
        image: resourceDto._links.resourceImage,
        self: resourceDto._links.self,
    };
}
