import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
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
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService,
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }

    public getResources(neighborhood: string): Observable<Resource[]> {
        return this.http.get<ResourceDto[]>(`${neighborhood}/resources`, { headers: this.headers }).pipe(
            mergeMap((resourcesDto: ResourceDto[]) => {
                const resourceObservables = resourcesDto.map(resourceDto =>
                    forkJoin([
                        this.http.get<ImageDto>(resourceDto._links.image),
                        this.http.get<NeighborhoodDto>(resourceDto._links.neighborhood)
                    ]).pipe(
                        map(([image, neighborhood]) => {
                            return {
                                title: resourceDto.title,
                                description: resourceDto.description,
                                image: image,
                                neighborhood: neighborhood,
                                self: resourceDto._links.self
                            } as Resource;
                        })
                    )
                );

                 return forkJoin(resourceObservables);
            })
        );
    }

    public addResource(neighborhood: string, resource: ResourceForm): Observable<ResourceForm> {
        return this.http.post<ResourceForm>(`${neighborhood}/resources`, resource, { headers: this.headers })
    }

    public updateResource(resource, resourceForm: ResourceForm): Observable<ResourceForm> {
        return this.http.patch<ResourceForm>(`${resource}`, resourceForm, { headers: this.headers })
    }

    public deleteResource(resource: string): Observable<void> {
        return this.http.delete<void>(resource, { headers: this.headers })
    }

}
