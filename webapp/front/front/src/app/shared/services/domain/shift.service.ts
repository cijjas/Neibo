import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Shift } from '../../models/index';
import { ShiftDto } from '../../dtos/app-dtos';

@Injectable({ providedIn: 'root' })
export class ShiftService {
    constructor(private http: HttpClient) { }

    public getShift(url: string): Observable<Shift> {
        return this.http.get<ShiftDto>(url).pipe(
            map((shiftDto: ShiftDto) => mapShift(shiftDto))
        );
    }

    public getShifts(url: string): Observable<Shift[]> {
        let params = new HttpParams();
        // QP forAmenity=amenityUrl
        // QP forDate=YYYY-MM-DD

        return this.http.get<ShiftDto[]>(url, { params }).pipe(
            map((shiftsDto: ShiftDto[]) => shiftsDto.map(mapShift))
        );
    }
}

export function mapShift(shiftDto: ShiftDto): Shift {
    return {
        startTime: shiftDto.startTime,
        day: shiftDto.day,
        self: shiftDto._links.self
    };
}
