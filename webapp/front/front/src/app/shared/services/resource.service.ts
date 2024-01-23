import { HttpClient } from '@angular/common/http'
import { Resource, ResourceDto, ResourceForm } from '../models/resource'
import { ImageDto } from "../models/image"
import { NeighborhoodDto } from "../models/neighborhood"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class ResourceService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getResources(neighborhoodId: number): Observable<Resource[]> {
        return this.http.get<ResourceDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/resources`).pipe(
            mergeMap((resourcesDto: ResourceDto[]) => {
                const resourceObservables = resourcesDto.map(resourceDto =>
                    forkJoin([
                        this.http.get<ImageDto>(resourceDto.image),
                        this.http.get<NeighborhoodDto>(resourceDto.neighborhood)
                    ]).pipe(
                        map(([image, neighborhood]) => {
                            return {
                                resourceId: resourceDto.resourceId,
                                title: resourceDto.title,
                                description: resourceDto.description,
                                image: image,
                                neighborhood: neighborhood,
                                self: resourceDto.self
                            } as Resource;
                        })
                    )
                );

                 return forkJoin(resourceObservables);
            })
        );
    }

    public addResource(neighborhoodId: number, resource: ResourceForm): Observable<ResourceForm> {
        return this.http.post<ResourceForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/resources`, resource)
    }

    public updateResource(neighborhoodId: number, resource: ResourceForm): Observable<ResourceForm> {
        return this.http.patch<ResourceForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/resources/${resource.resourceId}`, resource)
    }

    public deleteResource(neighborhoodId: number, resourceId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/resources/${resourceId}`)
    }

}
