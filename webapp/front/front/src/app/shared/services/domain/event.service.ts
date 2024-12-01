import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Event } from '../../models/index';
import { EventDto } from '../../dtos/app-dtos';
import { parseLinkHeader } from './utils';

@Injectable({ providedIn: 'root' })
export class EventService {
    constructor(private http: HttpClient) { }

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
}

export function mapEvent(eventDto: EventDto): Event {
    return {
        name: eventDto.name,
        description: eventDto.description,
        eventDate: eventDto.eventDate,
        startTime: eventDto.startTime,
        endTime: eventDto.endTime,
        attendeesCount: null, // TODO eventDto.attendeesCount
        self: eventDto._links.self
    };
}