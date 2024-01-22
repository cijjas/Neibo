import { HttpClient, HttpParams } from '@angular/common/http'
import { Amenity, AmenityDto, AmenityForm } from './amenity'
import { NeighborhoodDto } from './neighborhood'
import { AvailabilityDto } from './availability'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class AmenityService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getAmenity(amenityId: number, neighborhoodId: number): Observable<Amenity> {
        const amenityDto$ = this.http.get<AmenityDto>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities/${amenityId}`);
            
        return amenityDto$.pipe(
            mergeMap((amenityDto: AmenityDto) => {
                return forkJoin([
                    this.http.get<NeighborhoodDto>(amenityDto.neighborhood),
                    this.http.get<AvailabilityDto[]>(amenityDto.availability)
                ]).pipe(
                    map(([neighborhood, availabilities]) => {
                        return {
                            amenityId: amenityDto.amenityId,
                            name: amenityDto.name,
                            description: amenityDto.description,
                            neighborhood: neighborhood,
                            availability: availabilities,
                            self: amenityDto.self
                        } as Amenity;
                    })
                );
            })
        );
    }

    public getAmenities(neighborhoodId: number, page: number, size: number): Observable<Amenity[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
            
        return this.http.get<AmenityDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities`, { params }).pipe(
            mergeMap((amenitiesDto: AmenityDto[]) => {
                const amenityObservables = amenitiesDto.map(amenityDto => 
                    forkJoin([
                        this.http.get<NeighborhoodDto>(amenityDto.neighborhood),
                        //this.http.get<AvailabilityDto[]>(amenityDto.availability)
                    ]).pipe(
                        map(([neighborhood, /*availabilities*/]) => {
                            return {
                                amenityId: amenityDto.amenityId,
                                name: amenityDto.name,
                                description: amenityDto.description,
                                neighborhood: neighborhood,
                                //availability: availabilities,
                                self: amenityDto.self
                            } as Amenity;
                        })
                    )
                );
        
                 return forkJoin(amenityObservables);
            })
        );
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
