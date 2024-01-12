import { HttpClient } from '@angular/common/http';
import {Amenity} from './amenity'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class AmenityService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getAmenities(): Observable<Amenity[]> {
        return this.http.get<Amenity[]>(`${this.apiServerUrl}/neighborhoods/1/amenities`)
    }

    public addAmenity(amenity: Amenity): Observable<Amenity> {
        return this.http.post<Amenity>(`${this.apiServerUrl}/neighborhoods/1/amenities`, amenity)
    }

    public updateAmenity(amenity: Amenity): Observable<Amenity> {
        return this.http.put<Amenity>(`${this.apiServerUrl}/neighborhoods/1/amenities/${amenity.amenityId}`, amenity)
    }

    public deleteAmenity(amenityId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/neighborhoods/1/amenities/${amenityId}`)
    }
}