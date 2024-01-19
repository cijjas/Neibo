import { HttpClient, HttpParams } from '@angular/common/http'
import { NeighborhoodForm } from './neighborhoodForm'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class NeighborhoodService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getNeighborhoods(page: number, size: number): Observable<NeighborhoodForm[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString())

        return this.http.get<NeighborhoodForm[]>(`${this.apiServerUrl}/neighborhoods`, { params })
    }

    public getNeighborhoodById(neighborhoodId: number): Observable<NeighborhoodForm> {
        return this.http.get<NeighborhoodForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}`)
    }

    public addNeighborhood(neighborhood: NeighborhoodForm): Observable<NeighborhoodForm> {
        return this.http.post<NeighborhoodForm>(`${this.apiServerUrl}/neighborhoods`, neighborhood)
    }
}
