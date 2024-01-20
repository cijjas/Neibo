import { HttpClient, HttpParams } from '@angular/common/http'
import { AmenityForm } from './amenityForm'
import { Amenity } from './amenity'
import { AmenityDto } from './amenityDto'
import { NeighborhoodService } from './neighborhood.service'
import { AvailabilityService } from './availability.service'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class AmenityService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(
        private http: HttpClient,
        private neighborhoodService: NeighborhoodService,
        private availabilityService: AvailabilityService
        ) { }

    public getAmenity(amenityId: number, neighborhoodId: number): Observable<Amenity> {
        const amenityDto$ = this.http.get<AmenityDto>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities/${amenityId}`);
            
        return amenityDto$.pipe(
            mergeMap((amenityDto: AmenityDto) => {
                // Use forkJoin to make parallel requests for associated data
                return forkJoin([
                    this.neighborhoodService.getNeighborhood(neighborhoodId),
                    this.availabilityService.getAvailabilities(neighborhoodId, amenityId, 1, 10) // Adjust parameters as needed
                ]).pipe(
                    map(([neighborhood, availabilities]) => {
                        // Construct the Amenity object using the retrieved data
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
                // Use forkJoin to make parallel requests for associated data for each AmenityDto
                const amenitiesObservables = amenitiesDto.map(amenityDto => 
                    forkJoin([
                        this.neighborhoodService.getNeighborhood(neighborhoodId),
                        this.availabilityService.getAvailabilities(neighborhoodId, amenityDto.amenityId, 1, 10) // Adjust parameters as needed
                    ]).pipe(
                        map(([neighborhood, availabilities]) => {
                            // Construct the Amenity object using the retrieved data
                            return {
                                amenityId: amenityDto.amenityId,
                                name: amenityDto.name,
                                description: amenityDto.description,
                                neighborhood: neighborhood,
                                availability: availabilities,
                                self: amenityDto.self
                            } as Amenity;
                        })
                    )
                );
        
                 return forkJoin(amenitiesObservables);
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
