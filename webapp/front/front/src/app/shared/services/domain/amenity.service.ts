import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Amenity, Shift } from '../../models/index';
import { AmenityDto, ShiftDto } from '../../dtos/app-dtos';
import { mapShift } from './shift.service';

@Injectable({ providedIn: 'root' })
export class AmenityService {
    constructor(private http: HttpClient) { }

    public getAmenity(amenityUrl: string): Observable<Amenity> {
        return this.http.get<AmenityDto>(amenityUrl).pipe(
            mergeMap((amenityDto: AmenityDto) => mapAmenity(this.http, amenityDto))
        );
    }

    public getAmenities(amenitiesUrl: string, page: number, size: number): Observable<Amenity[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<AmenityDto[]>(amenitiesUrl, { params }).pipe(
            mergeMap((amenitiesDto: AmenityDto[]) => {
                const amenityObservables = amenitiesDto.map((amenityDto) =>
                    mapAmenity(this.http, amenityDto)
                );
                return forkJoin(amenityObservables);
            })
        );
    }
}

export function mapAmenity(http: HttpClient, amenityDto: AmenityDto): Observable<Amenity> {
    return forkJoin([
        http.get<ShiftDto[]>(amenityDto._links.shifts)
    ]).pipe(
        map(([shiftsDto]) => {
            const shifts: Shift[] = shiftsDto.map(mapShift);

            return {
                name: amenityDto.name,
                description: amenityDto.description,
                shiftsAvailable: shifts,
                self: amenityDto._links.self
            } as Amenity;
        })
    );
}
