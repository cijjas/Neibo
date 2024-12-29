import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Shift, ShiftDto } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class ShiftService {
    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService
    ) { }

    public getShift(url: string): Observable<Shift> {
        return this.http.get<ShiftDto>(url).pipe(
            map((shiftDto: ShiftDto) => mapShift(shiftDto))
        );
    }

    public getShifts(
        queryParams: {
            forAmenity?: string;
            forDate?: string;
        } = {}
    ): Observable<Shift[]> {
        let shiftsUrl: string = this.linkService.getLink('neighborhood:shifts')

        let params = new HttpParams();

        if (queryParams.forAmenity) params = params.set('forAmenity', queryParams.forAmenity);
        if (queryParams.forDate) params = params.set('forDate', queryParams.forDate);

        return this.http.get<ShiftDto[]>(shiftsUrl, { params }).pipe(
            map((shiftsDto) => shiftsDto.map(mapShift))
        );
    }
}

export function mapShift(shiftDto: ShiftDto): Shift {
    return {
        startTime: shiftDto.startTime,
        endTime: addOneHour(shiftDto.startTime),
        day: shiftDto.day,
        taken: shiftDto.isBooked,
        self: shiftDto._links.self,
    };
}

function addOneHour(time: string): string {
    const [hours, minutes, seconds] = time.split(':').map(Number);
    const date = new Date();
    date.setHours(hours, minutes, seconds);
    date.setHours(date.getHours() + 1);

    return date.toTimeString().split(' ')[0];
}
