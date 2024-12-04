import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Event } from '../../models/index';
import { EventDto } from '../../dtos/app-dtos';
import { parseLinkHeader } from './utils';
import { HateoasLinksService } from '../index.service';

@Injectable({ providedIn: 'root' })
export class EventService {
    constructor(
        private http: HttpClient,
        private linkStorage: HateoasLinksService
    ) { }

    public getEvent(url: string): Observable<Event> {
        return this.http.get<EventDto>(url).pipe(
            map((eventDto: EventDto) => mapEvent(eventDto))
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
            map((response) => {
                const eventsDto: EventDto[] = response.body || [];
                const pagination = parseLinkHeader(response.headers.get('Link'));

                const events = eventsDto.map(mapEvent);

                return {
                    events,
                    totalPages: pagination.totalPages,
                    currentPage: pagination.currentPage
                };
            })
        );
    }

    public getEventsForDateRange(
        url: string,
        dates: string[]
    ): Observable<Event[]> {
        const requests = dates.map((date) => {
            return this.getEvents(url, { forDate: date }).pipe(
                map((response) => response.events)
            );
        });

        return forkJoin(requests).pipe(
            map((eventsArrays) => eventsArrays.flat())
        );
    }

    public createEvent(event: Partial<Event>): Observable<Event> {
        const eventsUrl = this.linkStorage.getLink('neighborhood:events')

        const eventPayload = {
            name: event.name,
            description: event.description,
            eventDate: event.eventDate,
            startTime: event.startTime,
            endTime: event.endTime
        };

        return this.http.post<EventDto>(eventsUrl, eventPayload).pipe(
            map((eventDto) => mapEvent(eventDto))
        );
    }

    public deleteEvent(url: string): Observable<void> {
        return this.http.delete<void>(url);
    }
}

export function mapEvent(eventDto: EventDto): Event {
    return {
        name: eventDto.name,
        description: eventDto.description,
        eventDate: eventDto.eventDate,
        startTime: eventDto.startTime,
        endTime: eventDto.endTime,
        duration: calculateDurationInMinutes(eventDto.startTime, eventDto.endTime),
        attendeesCount: null, // TODO eventDto.attendeesCount
        self: eventDto._links.self
    };
}

function calculateDurationInMinutes(startTime: string, endTime: string): number {
    const [startHours, startMinutes] = startTime.split(':').map(Number);
    const [endHours, endMinutes] = endTime.split(':').map(Number);

    const startDate = new Date(0, 0, 0, startHours, startMinutes); // Arbitrary date
    const endDate = new Date(0, 0, 0, endHours, endMinutes);

    const durationMilliseconds = endDate.getTime() - startDate.getTime();
    return durationMilliseconds / (1000 * 60); // Convert milliseconds to minutes
}
