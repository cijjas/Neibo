import { HttpClient, HttpParams } from '@angular/common/http'
import { Attendance } from './attendance'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class AttendanceService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getAttendanceById(attendanceId : number, neighborhoodId : number, eventId : number): Observable<Attendance> {
        return this.http.get<Attendance>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${eventId}/attendance/${attendanceId}`)
    }

    public getAttendance(neighborhoodId : number, eventId : number, page : number, size : number): Observable<Attendance[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString())
        return this.http.get<Attendance[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${eventId}/attendance`)
    }

    public addAttendance(attendance: Attendance, neighborhoodId : number, eventId : number): Observable<Attendance> {
        return this.http.post<Attendance>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${eventId}/attendance`, attendance)
    }

    public deleteAttendance(attendanceId: number, neighborhoodId : number, eventId : number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${eventId}/attendance/${attendanceId}`)
    }
}