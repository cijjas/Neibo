import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { Attendance, AttendanceDto, AttendanceForm } from '../models/attendance'
import { UserDto } from '../models/user'
import { EventDto } from '../models/event'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'
import { LoggedInService } from './loggedIn.service'

@Injectable({providedIn: 'root'})
export class AttendanceService {
    private apiServerUrl = environment.apiBaseUrl
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }

    public getAttendanceById(attendance: string): Observable<Attendance> {
        const attendanceDto$ = this.http.get<AttendanceDto>(attendance, { headers: this.headers });

        return attendanceDto$.pipe(
            mergeMap((attendanceDto: AttendanceDto) => {
                return forkJoin([
                    this.http.get<UserDto>(attendanceDto._links.user),
                    this.http.get<EventDto>(attendanceDto._links.event)
                ]).pipe(
                    map(([user, event]) => {
                        return {
                            user: user,
                            event: event,
                            self: attendanceDto._links.self
                        } as Attendance;
                    })
                );
            })
        );
    }

    public getAttendance(event : string, page : number, size : number): Observable<Attendance[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());

        return this.http.get<AttendanceDto[]>(`${event}/attendance`, { params, headers: this.headers }).pipe(
            mergeMap((attendancesDto: AttendanceDto[]) => {
                const attendanceObservables = attendancesDto.map(attendanceDto =>
                    forkJoin([
                        this.http.get<UserDto>(attendanceDto._links.user),
                        this.http.get<EventDto>(attendanceDto._links.event)
                    ]).pipe(
                        map(([user, event]) => {
                            return {
                                user: user,
                                event: event,
                                self: attendanceDto._links.self
                            } as Attendance;
                        })
                    )
                );

                 return forkJoin(attendanceObservables);
            })
        );
    }

    public addAttendance(attendance: AttendanceForm, event: string): Observable<AttendanceForm> {
        return this.http.post<AttendanceForm>(`${event}/attendance`, attendance, { headers: this.headers })
    }

    public deleteAttendance(attendance: string): Observable<void> {
        return this.http.delete<void>(attendance, { headers: this.headers })
    }
}
