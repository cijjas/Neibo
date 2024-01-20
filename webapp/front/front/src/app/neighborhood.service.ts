import { HttpClient, HttpParams } from '@angular/common/http'
import { NeighborhoodForm } from './neighborhoodForm'
import { Neighborhood } from './neighborhood'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class NeighborhoodService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getNeighborhoods(page: number, size: number): Observable<Neighborhood[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString())

        return this.http.get<Neighborhood[]>(`${this.apiServerUrl}/neighborhoods`, { params })
    }

    public getNeighborhood(neighborhoodId: number): Observable<Neighborhood> {
        return this.http.get<Neighborhood>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}`)
    }

    public addNeighborhood(neighborhood: NeighborhoodForm): Observable<NeighborhoodForm> {
        return this.http.post<NeighborhoodForm>(`${this.apiServerUrl}/neighborhoods`, neighborhood)
    }
}
