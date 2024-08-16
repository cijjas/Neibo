import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { Booking, BookingDto, BookingForm } from '../models/booking'
import { UserDto } from '../models/user'
import { AvailabilityDto } from '../models/availability'
import { map, mergeMap } from 'rxjs/operators'
import { ShiftDto } from '../models/shift'
import { LoggedInService } from './loggedIn.service'

@Injectable({providedIn: 'root'})
export class BookingService {
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

    public getBooking(booking: string): Observable<Booking> {
        const bookingDto$ = this.http.get<BookingDto>(booking, { headers: this.headers });

        return bookingDto$.pipe(
            mergeMap((bookingDto: BookingDto) => {
                return forkJoin([
                    this.http.get<UserDto>(bookingDto._links.user),
                    this.http.get<ShiftDto>(bookingDto._links.shift)
                ]).pipe(
                    map(([user, shift]) => {
                        return {
                            bookingDate: bookingDto.bookingDate,
                            user: user,
                            shift: shift,
                            self: bookingDto._links.self
                        } as Booking;
                    })
                );
            })
        );
    }

    public getBookings(neighborhood: string, user: string, amenity: string): Observable<Booking[]> {
        let params = new HttpParams();
        
        if(user) params = params.set('bookedBy', user)
        if(amenity) params = params.set('forAmenity', amenity)

        return this.http.get<BookingDto[]>(`${neighborhood}/bookings`, { params, headers: this.headers }).pipe(
            mergeMap((bookingsDto: BookingDto[]) => {
                const bookingObservables = bookingsDto.map(bookingDto =>
                    forkJoin([
                        this.http.get<UserDto>(bookingDto._links.user),
                        this.http.get<ShiftDto>(bookingDto._links.shift)
                    ]).pipe(
                        map(([user, shift]) => {
                            return {
                                bookingDate: bookingDto.bookingDate,
                                user: user,
                                shift: shift,
                                self: bookingDto._links.self
                            } as Booking;
                        })
                    )
                );

                 return forkJoin(bookingObservables);
            })
        );
    }

    public addBooking(booking: BookingForm, neighborhood : string): Observable<BookingForm> {
        return this.http.post<BookingForm>(`${neighborhood}/bookings`, booking, { headers: this.headers })
    }

}
