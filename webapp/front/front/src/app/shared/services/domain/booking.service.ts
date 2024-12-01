import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Booking } from '../../models/index';
import { BookingDto, AmenityDto, ShiftDto } from '../../dtos/app-dtos';
import { mapShift } from './shift.service';
import { mapAmenity } from './amenity.service';
import { parseLinkHeader } from './utils';

@Injectable({ providedIn: 'root' })
export class BookingService {
    constructor(private http: HttpClient) { }

    public getBooking(url: string): Observable<Booking> {
        return this.http.get<BookingDto>(url).pipe(
            mergeMap((bookingDto: BookingDto) => mapBooking(this.http, bookingDto))
        );
    }

    public getBookings(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
            bookedBy?: string;
            forAmenity?: string;
        } = {}
    ): Observable<{ bookings: Booking[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.bookedBy) params = params.set('bookedBy', queryParams.bookedBy);
        if (queryParams.forAmenity) params = params.set('forAmenity', queryParams.forAmenity);

        return this.http.get<BookingDto[]>(url, { params, observe: 'response' }).pipe(
            mergeMap((response) => {
                const bookingsDto: BookingDto[] = response.body || [];
                const pagination = parseLinkHeader(response.headers.get('Link'));

                const bookingObservables = bookingsDto.map((bookingDto) =>
                    mapBooking(this.http, bookingDto)
                );

                return forkJoin(bookingObservables).pipe(
                    map((bookings) => ({
                        bookings,
                        totalPages: pagination.totalPages,
                        currentPage: pagination.currentPage,
                    }))
                );
            })
        );
    }
}

export function mapBooking(http: HttpClient, bookingDto: BookingDto): Observable<Booking> {
    return forkJoin([
        http.get<AmenityDto>(bookingDto._links.amenity).pipe(mergeMap(amenityDto => mapAmenity(http, amenityDto))),
        http.get<ShiftDto>(bookingDto._links.shift)
    ]).pipe(
        map(([amenity, shiftDto]) => {
            return {
                shift: mapShift(shiftDto),
                amenity: amenity,
                bookingDate: bookingDto.bookingDate,
                self: bookingDto._links.self
            } as Booking;
        })
    );
}