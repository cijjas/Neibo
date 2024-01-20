import { HttpClient, HttpParams } from '@angular/common/http'
import { Shift, ShiftDto } from './shift'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class ShiftService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getShifts(amenityId: number, dayId: number, date: string): Observable<Shift[]> {
        const params = new HttpParams().set('amenityId', amenityId.toString()).set('dayId', dayId.toString()).set('date', date)

        return this.http.get<Shift[]>(`${this.apiServerUrl}/shifts`, { params })
    }
}
