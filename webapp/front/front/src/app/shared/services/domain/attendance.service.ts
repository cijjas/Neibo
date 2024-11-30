import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Attendance } from '../../models/index';
import { EventDto, UserDto, AttendanceDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';
import { mapEvent } from './event.service';

@Injectable({ providedIn: 'root' })
export class AttendanceService {
    constructor(private http: HttpClient) { }

    public getAttendance(url: string): Observable<Attendance> {
        return this.http.get<AttendanceDto>(url).pipe(
            mergeMap((attendanceDto: AttendanceDto) => mapAttendance(this.http, attendanceDto))
        );
    }

    public getAttendances(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
        } = {}
    ): Observable<Attendance[]> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http.get<AttendanceDto[]>(url, { params }).pipe(
            mergeMap((attendancesDto: AttendanceDto[]) => {
                const attendanceObservables = attendancesDto.map(attendanceDto =>
                    mapAttendance(this.http, attendanceDto)
                );
                return forkJoin(attendanceObservables);
            })
        );
    }

}

export function mapAttendance(http: HttpClient, attendanceDto: AttendanceDto): Observable<Attendance> {
    return forkJoin([
        http.get<UserDto>(attendanceDto._links.attendanceUser).pipe(mergeMap(userDto => mapUser(http, userDto))),
        http.get<EventDto>(attendanceDto._links.event)
    ]).pipe(
        map(([user, eventDto]) => {
            return {
                user: user,
                event: mapEvent(eventDto),
                self: attendanceDto._links.self
            } as Attendance;
        })
    );
}
