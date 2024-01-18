import { HttpClient, HttpParams } from '@angular/common/http'
import { Amenity } from './amenity'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class AmenityService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getAmenity(amenityId : number, neighborhoodId : number): Observable<Amenity> {
        return this.http.get<Amenity>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities/${amenityId}`)
    }

    public getAmenities(neighborhoodId : number, page : number, size : number): Observable<Amenity[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString())
        return this.http.get<Amenity[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities`)
    }

    public addAmenity(amenity: Amenity, neighborhoodId : number): Observable<Amenity> {
        return this.http.post<Amenity>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities`, amenity)
    }

    public updateAmenity(amenity: Amenity, neighborhoodId : number): Observable<Amenity> {
        return this.http.patch<Amenity>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities/${amenity.amenityId}`, amenity)
    }

    public deleteAmenity(amenityId: number, neighborhoodId : number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities/${amenityId}`)
    }
}