import { HttpClient, HttpParams } from '@angular/common/http'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { Booking, BookingDto, BookingForm } from '../models/booking'
import { UserDto } from '../models/user'
import { AvailabilityDto } from '../models/availability'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class BookingService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getBooking(bookingId : number, neighborhoodId : number): Observable<Booking> {
        const bookingDto$ = this.http.get<BookingDto>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/bookings/${bookingId}`);

        return bookingDto$.pipe(
            mergeMap((bookingDto: BookingDto) => {
                return forkJoin([
                    this.http.get<UserDto>(bookingDto.user),
                    this.http.get<AvailabilityDto>(bookingDto.amenityAvailability)
                ]).pipe(
                    map(([user, availability]) => {
                        return {
                            bookingId: bookingDto.bookingId,
                            bookingDate: bookingDto.bookingDate,
                            user: user,
                            amenityAvailability: availability,
                            self: bookingDto.self
                        } as Booking;
                    })
                );
            })
        );
    }

    public getBookings(neighborhoodId : number, userId : number, amenityId : number): Observable<Booking[]> {
        const params = new HttpParams().set('userId', userId.toString()).set('amenityId', amenityId.toString());

        return this.http.get<BookingDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/bookings`, { params }).pipe(
            mergeMap((bookingsDto: BookingDto[]) => {
                const bookingObservables = bookingsDto.map(bookingDto =>
                    forkJoin([
                        this.http.get<UserDto>(bookingDto.user),
                        this.http.get<AvailabilityDto>(bookingDto.amenityAvailability)
                    ]).pipe(
                        map(([user, availability]) => {
                            return {
                                bookingId: bookingDto.bookingId,
                                bookingDate: bookingDto.bookingDate,
                                user: user,
                                amenityAvailability: availability,
                                self: bookingDto.self
                            } as Booking;
                        })
                    )
                );

                 return forkJoin(bookingObservables);
            })
        );
    }

    public addBooking(booking: BookingForm, neighborhoodId : number): Observable<BookingForm> {
        return this.http.post<BookingForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/bookings`, booking)
    }

}
