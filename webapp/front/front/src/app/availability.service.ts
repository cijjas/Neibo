import { HttpClient, HttpParams } from '@angular/common/http'
import { Availability, AvailabilityDto } from './availability'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class AvailabilityService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getAvailabilities(neighborhoodId : number, amenityId : number, page: number, size: number): Observable<Availability[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString())

        return this.http.get<Availability[]>(`${this.apiServerUrl}/neighborhoods/{neighborhoodId}/amenities/{amenityId}/availability`, { params })
    }

    public getAvailability(availabilityId: number, neighborhoodId : number, amenityId : number,): Observable<Availability> {
        return this.http.get<Availability>(`${this.apiServerUrl}/neighborhoods/{neighborhoodId}/amenities/{amenityId}/availability/{availabilityId}`)
    }

}
