import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { Booking, mapShift, BookingDto, AmenityDto, ShiftDto, mapAmenity, parseLinkHeader, LinkKey } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class BookingService {
    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService,
    ) { }

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

    public createBooking(amenity: string, bookingDate: string, shifts: string[], user: string): Observable<(string | null)[]> {
        return forkJoin(
            shifts.map(shift =>
                this.http.post(this.linkService.getLink(LinkKey.NEIGHBORHOOD_BOOKINGS), {
                    amenity,
                    bookingDate,
                    shift,
                    user
                }, { observe: 'response' }).pipe(
                    map(response => {
                        const locationHeader = response.headers.get('Location');
                        if (locationHeader) {
                            return locationHeader;
                        } else {
                            console.error('Location header not found for shift:', shift);
                            return null;
                        }
                    }),
                    catchError(error => {
                        console.error('Error creating booking for shift:', shift, error);
                        return of(null);
                    })
                )
            )
        );
    }

    public deleteBooking(url: string): Observable<void> {
        return this.http.delete<void>(url);
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