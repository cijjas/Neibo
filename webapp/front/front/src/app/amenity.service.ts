import { HttpClient, HttpParams } from '@angular/common/http'
import { AmenityForm } from './amenityForm'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class AmenityService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getAmenity(amenityId : number, neighborhoodId : number): Observable<AmenityForm> {
        return this.http.get<AmenityForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities/${amenityId}`)
    }

    public getAmenities(neighborhoodId : number, page : number, size : number): Observable<AmenityForm[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString())
        return this.http.get<AmenityForm[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities`)
    }

    public addAmenity(amenity: AmenityForm, neighborhoodId : number): Observable<AmenityForm> {
        return this.http.post<AmenityForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities`, amenity)
    }

    public updateAmenity(amenity: AmenityForm, neighborhoodId : number): Observable<AmenityForm> {
        return this.http.patch<AmenityForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities/${amenity.amenityId}`, amenity)
    }

    public deleteAmenity(amenityId: number, neighborhoodId : number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities/${amenityId}`)
    }
}
