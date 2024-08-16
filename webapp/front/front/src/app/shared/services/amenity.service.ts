import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Amenity, AmenityDto, AmenityForm } from '../models/amenity'
import { NeighborhoodDto } from '../models/neighborhood'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'
import { ShiftDto } from '../models/shift'

@Injectable({providedIn: 'root'})
export class AmenityService {
    private apiServerUrl = environment.apiBaseUrl
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService,
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }

    public getAmenity(amenity: string): Observable<Amenity> {
        const amenityDto$ = this.http.get<AmenityDto>(amenity, { headers: this.headers });

        return amenityDto$.pipe(
            mergeMap((amenityDto: AmenityDto) => {
                return forkJoin([
                    this.http.get<NeighborhoodDto>(amenityDto._links.neighborhood),
                    this.http.get<ShiftDto[]>(amenityDto._links.shifts)
                ]).pipe(
                    map(([neighborhood, shifts]) => {
                        return {
                            name: amenityDto.name,
                            description: amenityDto.description,
                            neighborhood: neighborhood,
                            shifts: shifts,
                            self: amenityDto._links.self
                        } as Amenity;
                    })
                );
            })
        );
    }

    public getAmenities(neighborhood: string, page: number, size: number): Observable<Amenity[]> {
        let params = new HttpParams()
        if(page) params = params.set('page', page.toString())
        if(size) params = params.set('size', size.toString())

        return this.http.get<AmenityDto[]>(`${neighborhood}/amenities`, { params, headers: this.headers }).pipe(
            mergeMap((amenitiesDto: AmenityDto[]) => {
                const amenityObservables = amenitiesDto.map(amenityDto =>
                    forkJoin([
                        this.http.get<NeighborhoodDto>(amenityDto._links.neighborhood),
                        this.http.get<ShiftDto[]>(amenityDto._links.shifts)
                    ]).pipe(
                        map(([neighborhood, shifts]) => {
                            return {
                                name: amenityDto.name,
                                description: amenityDto.description,
                                neighborhood: neighborhood,
                                shifts: shifts,
                                self: amenityDto._links.self
                            } as Amenity;
                        })
                    )
                );

                 return forkJoin(amenityObservables);
            })
        );
    }

    public addAmenity(amenity: AmenityForm, neighborhood : string): Observable<AmenityForm> {
        return this.http.post<AmenityForm>(`${neighborhood}/amenities`, amenity, { headers: this.headers })
    }

    public updateAmenity(amenityForm: AmenityForm, amenity : string): Observable<AmenityForm> {
        return this.http.patch<AmenityForm>(amenity, amenityForm, { headers: this.headers })
    }

    public deleteAmenity(amenity: string): Observable<void> {
        return this.http.delete<void>(amenity, { headers: this.headers })
    }
}
