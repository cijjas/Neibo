import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Shift } from '../../models/index';
import { ShiftDto } from '../../dtos/app-dtos';
import { parseLinkHeader } from './utils';

@Injectable({ providedIn: 'root' })
export class ShiftService {
    constructor(private http: HttpClient) { }

    public getShift(url: string): Observable<Shift> {
        return this.http.get<ShiftDto>(url).pipe(
            map((shiftDto: ShiftDto) => mapShift(shiftDto))
        );
    }

    public getShifts(
        url: string,
        queryParams: {
            forAmenity?: string;
            forDate?: string;
            page?: number;
            size?: number;
        } = {}
    ): Observable<{ shifts: Shift[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.forAmenity) params = params.set('forAmenity', queryParams.forAmenity);
        if (queryParams.forDate) params = params.set('forDate', queryParams.forDate);
        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http.get<ShiftDto[]>(url, { params, observe: 'response' }).pipe(
            map((response) => {
                const shiftsDto: ShiftDto[] = response.body || [];
                const pagination = parseLinkHeader(response.headers.get('Link'));

                const shifts = shiftsDto.map(mapShift);

                return {
                    shifts,
                    totalPages: pagination.totalPages,
                    currentPage: pagination.currentPage,
                };
            })
        );
    }
}

export function mapShift(shiftDto: ShiftDto): Shift {
    return {
        startTime: shiftDto.startTime,
        day: shiftDto.day,
        self: shiftDto._links.self,
    };
}
