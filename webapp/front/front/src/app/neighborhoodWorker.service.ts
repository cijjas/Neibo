import { HttpClient, HttpParams } from '@angular/common/http'
import { Neighborhood, NeighborhoodDto, NeighborhoodForm } from './neighborhood'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class NeighborhoodWorkerService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getWorkersNeighborhoods(workerId: number): Observable<NeighborhoodDto[]> {
        return this.http.get<NeighborhoodDto[]>(`${this.apiServerUrl}/workers/${workerId}/neighborhoods`)
    }

    public addWorkerToNeighborhood(workerId: number, neighborhoodId: number): void {
        const params = new HttpParams().set('neighborhoodId', neighborhoodId.toString())
        this.http.patch<NeighborhoodForm>(`${this.apiServerUrl}/workers/${workerId}/neighborhoods`, {params})
    }

    public removeWorkerFromNeighborhood(workerId: number, neighborhoodId: number): void {
        const params = new HttpParams().set('neighborhoodId', neighborhoodId.toString())
        this.http.delete<Worker>(`${this.apiServerUrl}/workers/${workerId}/neighborhoods`, {params})
    }
}
