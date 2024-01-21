import { HttpClient, HttpParams } from '@angular/common/http'
import { Availability, AvailabilityDto } from './availability'
import { AmenityDto } from './amenity'
import { Shift, ShiftDto } from './shift'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class AvailabilityService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getAvailability(availabilityId: number, neighborhoodId : number, amenityId : number,): Observable<Availability> {
        const availabilityDto$ = this.http.get<AvailabilityDto>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities/${amenityId}/availability/${availabilityId}`);
            
        return availabilityDto$.pipe(
            mergeMap((availabilityDto: AvailabilityDto) => {
                return forkJoin([
                    this.http.get<AmenityDto>(availabilityDto.amenity),
                    this.http.get<ShiftDto>(availabilityDto.shift)
                ]).pipe(
                    map(([amenity, shift]) => {
                        return {
                            availabilityId: availabilityDto.availabilityId,
                            amenity: amenity,
                            shift: shift,
                            self: availabilityDto.self
                        } as Availability;
                    })
                );
            })
        );
    }

    public getAvailabilities(neighborhoodId : number, amenityId : number, status: string, date: string ): Observable<Availability[]> {
        const params = new HttpParams().set('status', status).set('date', date);
            
        return this.http.get<AvailabilityDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/amenities/${amenityId}/availability`, { params }).pipe(
            mergeMap((availabilitiesDto: AvailabilityDto[]) => {
                const availabilityObservables = availabilitiesDto.map(availabilityDto => 
                    forkJoin([
                        this.http.get<AmenityDto>(availabilityDto.amenity),
                        this.http.get<ShiftDto>(availabilityDto.shift)
                    ]).pipe(
                        map(([amenity, shift]) => {
                            return {
                                availabilityId: availabilityDto.availabilityId,
                                amenity: amenity,
                                shift: shift,
                                self: availabilityDto.self
                            } as Availability;
                        })
                    )
                );
        
                 return forkJoin(availabilityObservables);
            })
        );
    }

}
