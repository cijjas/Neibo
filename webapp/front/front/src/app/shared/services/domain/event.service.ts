import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Event } from '../../models/index';
import { EventDto } from '../../dtos/app-dtos';

@Injectable({ providedIn: 'root' })
export class EventService {
    constructor(private http: HttpClient) { }

    public getEvent(url: string): Observable<Event> {
        return this.http.get<EventDto>(url).pipe(
            map((eventDto: EventDto) => mapEvent(eventDto))
        );
    }

    public getEvents(url: string, page: number, size: number): Observable<Event[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<EventDto[]>(url, { params }).pipe(
            map((eventsDto: EventDto[]) => eventsDto.map(mapEvent))
        );
    }
}

export function mapEvent(eventDto: EventDto): Event {
    return {
        name: eventDto.name,
        description: eventDto.description,
        date: eventDto.eventDate,
        startTime: eventDto.startTime,
        endTime: eventDto.endTime,
        attendeesCount: null, // TODO eventDto.attendeesCount
        self: eventDto._links.self
    };
}

