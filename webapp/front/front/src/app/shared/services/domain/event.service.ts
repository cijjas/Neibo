import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { map, mergeMap, timeout, toArray } from 'rxjs/operators';
import { Event } from '../../models/index';
import { AttendanceCountDto, EventDto } from '../../dtos/app-dtos';
import { parseLinkHeader } from './utils';
import { HateoasLinksService } from '../index.service';
import { from } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class EventService {
    constructor(
        private http: HttpClient,
        private linkStorage: HateoasLinksService
    ) { }

    public getEvent(url: string): Observable<Event> {
        return this.http.get<EventDto>(url).pipe(
            mergeMap((eventDto: EventDto) => mapEvent(this.http, eventDto))
        );
    }


    public getEvents(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
            forDate?: string;
        } = {}
    ): Observable<{ events: Event[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.forDate) params = params.set('forDate', queryParams.forDate);

        return this.http.get<EventDto[]>(url, { params, observe: 'response' }).pipe(
            timeout(10000), // Set a 10-second timeout for the HTTP request
            mergeMap((response) => {
                const eventsDto: EventDto[] = response.body || [];
                const pagination = parseLinkHeader(response.headers.get('Link'));

                const eventObservables = eventsDto.map((eventDto) =>
                    mapEvent(this.http, eventDto)
                );

                return forkJoin(eventObservables).pipe(
                    map((events) => ({
                        events,
                        totalPages: pagination.totalPages,
                        currentPage: pagination.currentPage,
                    }))
                );
            })
        );
    }


    public getEventsForDateRange(
        url: string,
        dates: string[]
    ): Observable<Event[]> {
        return from(dates).pipe(
            mergeMap(
                (date) => this.getEvents(url, { forDate: date }).pipe(
                    map((response) => response.events),
                    catchError((error) => {
                        console.error(`Error fetching events for date ${date}:`, error);
                        return of([]); // Return an empty array on error to prevent breaking the stream
                    })
                ),
                5 // Limit concurrency to 5 requests at a time
            ),
            toArray(),
            map((eventsArrays) => eventsArrays.flat())
        );
    }

    public createEvent(event: Partial<Event>): Observable<Event> {
        const eventsUrl = this.linkStorage.getLink('neighborhood:events');

        const eventPayload = {
            name: event.name,
            description: event.description,
            eventDate: event.eventDate,
            startTime: event.startTime,
            endTime: event.endTime
        };

        return this.http.post<EventDto>(eventsUrl, eventPayload).pipe(
            mergeMap((eventDto) => mapEvent(this.http, eventDto))
        );
    }

    public deleteEvent(url: string): Observable<void> {
        return this.http.delete<void>(url);
    }
}

export function mapEvent(http: HttpClient, eventDto: EventDto): Observable<Event> {
    return forkJoin([
        http.get<AttendanceCountDto>(eventDto._links.attendanceCount),
    ]).pipe(
        map(([attendanceCount]) => {
            return {
                name: eventDto.name,
                description: eventDto.description,
                eventDate: eventDto.eventDate,
                startTime: eventDto.startTime,
                endTime: eventDto.endTime,
                duration: calculateDurationInMinutes(eventDto.startTime, eventDto.endTime),
                attendeesCount: attendanceCount.count,
                self: eventDto._links.self
            } as Event;
        })
    );
}

function calculateDurationInMinutes(startTime: string, endTime: string): number {
    const [startHours, startMinutes] = startTime.split(':').map(Number);
    const [endHours, endMinutes] = endTime.split(':').map(Number);

    const startDate = new Date(0, 0, 0, startHours, startMinutes); // Arbitrary date
    const endDate = new Date(0, 0, 0, endHours, endMinutes);

    const durationMilliseconds = endDate.getTime() - startDate.getTime();
    return durationMilliseconds / (1000 * 60); // Convert milliseconds to minutes
}
