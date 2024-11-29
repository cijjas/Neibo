import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Attendance } from '../model/attendance';
import { EventDto, UserDto, AttendanceDto } from '../dtos/app-dtos';
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

    public listAttendances(url: string): Observable<Attendance[]> {
        return this.http.get<AttendanceDto[]>(url).pipe(
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
        http.get<UserDto>(attendanceDto._links.user).pipe(mergeMap(userDto => mapUser(http, userDto))),
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
