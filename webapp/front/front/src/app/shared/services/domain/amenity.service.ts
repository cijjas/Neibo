import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Amenity, Shift } from '../../models/index';
import { AmenityDto, ShiftDto } from '../../dtos/app-dtos';
import { mapShift } from './shift.service';
import { parseLinkHeader } from './utils';

@Injectable({ providedIn: 'root' })
export class AmenityService {
    constructor(private http: HttpClient) { }

    public getAmenity(amenityUrl: string): Observable<Amenity> {
        return this.http.get<AmenityDto>(amenityUrl).pipe(
            mergeMap((amenityDto: AmenityDto) => mapAmenity(this.http, amenityDto))
        );
    }

    public getAmenities(
        amenitiesUrl: string,
        queryParams: {
            page?: number;
            size?: number
        } = {}
    ): Observable<{ amenities: Amenity[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http.get<AmenityDto[]>(amenitiesUrl, { params, observe: 'response' }).pipe(
            mergeMap((response) => {
                const amenitiesDto: AmenityDto[] = response.body || [];
                const pagination = parseLinkHeader(response.headers.get('Link'));

                const amenityObservables = amenitiesDto.map((amenityDto) =>
                    mapAmenity(this.http, amenityDto)
                );
                return forkJoin(amenityObservables).pipe(
                    map((amenities) => ({
                        amenities,
                        totalPages: pagination.totalPages,
                        currentPage: pagination.currentPage,
                    }))
                );
            })
        );
    }
}

export function mapAmenity(http: HttpClient, amenityDto: AmenityDto): Observable<Amenity> {
    return forkJoin([http.get<ShiftDto[]>(amenityDto._links.shifts)]).pipe(
        map(([shiftsDto]) => {
            const shifts: Shift[] = shiftsDto.map(mapShift);

            return {
                name: amenityDto.name,
                description: amenityDto.description,
                availableShifts: shifts,
                self: amenityDto._links.self,
            } as Amenity;
        })
    );
}
