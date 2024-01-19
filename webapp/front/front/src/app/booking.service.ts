import { HttpClient, HttpParams } from '@angular/common/http'
import { BookingForm } from './bookingForm'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class BookingService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getBooking(bookingId : number, neighborhoodId : number): Observable<BookingForm> {
        return this.http.get<BookingForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/bookings/${bookingId}`)
    }

    public getBookings(neighborhoodId : number, userId : number): Observable<BookingForm[]> {
        const params = new HttpParams().set('userId', userId.toString())
        return this.http.get<BookingForm[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/bookings`)
    }

    public addBooking(booking: BookingForm, neighborhoodId : number): Observable<BookingForm> {
        return this.http.post<BookingForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/bookings`, booking)
    }

}
