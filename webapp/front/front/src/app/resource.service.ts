import { HttpClient } from '@angular/common/http'
import { ResourceForm } from './resourceForm'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class ResourceService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getResources(neighborhoodId: number): Observable<ResourceForm[]> {
        return this.http.get<ResourceForm[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/resources`)
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
