
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { LoggedInService } from '../services/loggedIn.service';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Amenity } from '../model/amenity';
import { Shift } from '../model/shift';
import { AmenityDto, ShiftDto } from '../dtos/app-dtos';



@Injectable({ providedIn: 'root' })
export class AmenityService {

    constructor(
        private http: HttpClient
    ) { }

    public getAmenity(amenityUrl: string): Observable<Amenity> {
        const modelDto$ = this.http.get<AmenityDto>(amenityUrl);

        return modelDto$.pipe(
            mergeMap((amenityDto: AmenityDto) => {
                return forkJoin([
                    this.http.get<ShiftDto[]>(amenityDto._links.shifts),
                ]).pipe(
                    map(([shiftsDto]) => {
                        const shifts: Shift[] = shiftsDto.map((shiftDto: ShiftDto) => ({
                            startTime: shiftDto.startTime,
                            day: shiftDto.day,
                            self: shiftDto._links.self
                        }));

                        return {
                            name: amenityDto.name,
                            description: amenityDto.description,
                            shiftsAvailable: shifts,
                            self: amenityDto._links.self
                        } as Amenity;
                    })
                );
            })
        );
    }

    public listAmenities(
        amenitiesUrl: string,
        page: number,
        size: number): Observable<Amenity[]> {
        let params = new HttpParams()
        if (page) params = params.set('page', page.toString())
        if (size) params = params.set('size', size.toString())

        return this.http.get<AmenityDto[]>(amenitiesUrl, { params }).pipe(
            mergeMap((amenitiesDto: AmenityDto[]) => {
                const amenityObservables = amenitiesDto.map(amenityDto =>
                    forkJoin([
                        this.http.get<ShiftDto[]>(amenityDto._links.shifts),
                    ]).pipe(
                        map(([shiftsDto]) => {
                            const shifts: Shift[] = shiftsDto.map((shiftDto: ShiftDto) => ({
                                startTime: shiftDto.startTime,
                                day: shiftDto.day,
                                self: shiftDto._links.self
                            }));

                            return {
                                name: amenityDto.name,
                                description: amenityDto.description,
                                shiftsAvailable: shifts,
                                self: amenityDto._links.self
                            } as Amenity;
                        })
                    )
                );

                return forkJoin(amenityObservables);
            })
        );
    }
}