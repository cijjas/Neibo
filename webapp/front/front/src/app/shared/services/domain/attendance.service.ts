import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
// todoaca
import { Attendance, EventDto, UserDto, AttendanceDto, mapUser, mapEvent, parseLinkHeader } from '@shared/index';

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

    public createAttendance(
        attendanceUrl: string,
        userUrl: string
    ): Observable<string | null> {
        const body = {
            user: userUrl,
        };

        return this.http.post(attendanceUrl, body, { observe: 'response' }).pipe(
            map(response => {
                const locationHeader = response.headers.get('Location');
                if (locationHeader) {
                    return locationHeader;
                } else {
                    console.error('Location header not found:');
                    return null;
                }
            }),
            catchError(error => {
                console.error('Error creating attendance for User', userUrl, error);
                return of(null);
            })
        )
    }

    public deleteAttendance(attendanceUrl: string): Observable<void> {
        return this.http.delete<void>(attendanceUrl);
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