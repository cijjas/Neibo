import { HttpClient, HttpParams } from '@angular/common/http'
import { Attendance, AttendanceDto, AttendanceForm } from './attendance'
import { UserDto } from './user'
import { EventDto } from './event'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class AttendanceService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getAttendanceById(attendanceId : number, neighborhoodId : number, eventId : number): Observable<Attendance> {
        const attendanceDto$ = this.http.get<AttendanceDto>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${eventId}/attendance/${attendanceId}`);
            
        return attendanceDto$.pipe(
            mergeMap((attendanceDto: AttendanceDto) => {
                return forkJoin([
                    this.http.get<UserDto>(attendanceDto.user),
                    this.http.get<EventDto>(attendanceDto.event)
                ]).pipe(
                    map(([user, event]) => {
                        return {
                            attendanceId: attendanceDto.attendanceId,
                            user: user,
                            event: event,
                            self: attendanceDto.self
                        } as Attendance;
                    })
                );
            })
        );
    }

    public getAttendance(neighborhoodId : number, eventId : number, page : number, size : number): Observable<Attendance[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
            
        return this.http.get<AttendanceDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${eventId}/attendance`, { params }).pipe(
            mergeMap((attendancesDto: AttendanceDto[]) => {
                const attendanceObservables = attendancesDto.map(attendanceDto => 
                    forkJoin([
                        this.http.get<UserDto>(attendanceDto.user),
                        this.http.get<EventDto>(attendanceDto.event)
                    ]).pipe(
                        map(([user, event]) => {
                            return {
                                attendanceId: attendanceDto.attendanceId,
                                user: user,
                                event: event,
                                self: attendanceDto.self
                            } as Attendance;
                        })
                    )
                );
        
                 return forkJoin(attendanceObservables);
            })
        );
    }

    public addAttendance(attendance: AttendanceForm, neighborhoodId : number, eventId : number): Observable<AttendanceForm> {
        return this.http.post<AttendanceForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${eventId}/attendance`, attendance)
    }

    public deleteAttendance(attendanceId: number, neighborhoodId : number, eventId : number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${eventId}/attendance/${attendanceId}`)
    }
}
