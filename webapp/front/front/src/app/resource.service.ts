import { HttpClient } from '@angular/common/http';
import {Resource} from './resource'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class ResourceService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getResources(neighborhoodId: number): Observable<Resource[]> {    
        return this.http.get<Resource[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/resources`);
    }

    public addResource(neighborhoodId: number, resource: Resource): Observable<Resource> {
        return this.http.post<Resource>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/resources`, resource);
    }

    public updateResource(neighborhoodId: number, resource: Resource): Observable<Resource> {
        return this.http.patch<Resource>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/resources/${resource.resourceId}`, resource);
    }

    public deleteResource(neighborhoodId: number, resourceId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/resources/${resourceId}`);
    }

}