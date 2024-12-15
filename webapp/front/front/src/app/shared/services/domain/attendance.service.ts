import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Attendance } from '../../models/index';
import { EventDto, UserDto, AttendanceDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';
import { mapEvent } from './event.service';
import { parseLinkHeader } from './utils';

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
        queryParams: { page?: number; size?: number } = {}
    ): Observable<{ attendances: Attendance[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http.get<AttendanceDto[]>(url, { params, observe: 'response' }).pipe(
            mergeMap((response) => {
                const attendancesDto: AttendanceDto[] = response.body || [];
                const pagination = parseLinkHeader(response.headers.get('Link'));

                const attendanceObservables = attendancesDto.map((attendanceDto) =>
                    mapAttendance(this.http, attendanceDto)
                );

                return forkJoin(attendanceObservables).pipe(
                    map((attendances) => ({
                        attendances,
                        totalPages: pagination.totalPages,
                        currentPage: pagination.currentPage,
                    }))
                );
            })
        );
    }
}

export function mapAttendance(http: HttpClient, attendanceDto: AttendanceDto): Observable<Attendance> {
    return forkJoin([
        http.get<UserDto>(attendanceDto._links.attendanceUser).pipe(
            mergeMap(userDto => mapUser(http, userDto))
        ),
        http.get<EventDto>(attendanceDto._links.event).pipe(
            mergeMap(eventDto => mapEvent(http, eventDto))
        )
    ]).pipe(
        map(([user, event]) => {
            return {
                user: user,
                event: event,
                self: attendanceDto._links.self
            } as Attendance;
        })
    );
}