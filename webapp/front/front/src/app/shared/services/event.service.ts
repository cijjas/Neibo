import { HttpClient, HttpParams } from '@angular/common/http'
import { Event, EventDto, EventForm } from '../models/event'
import { NeighborhoodDto } from '../models/neighborhood'
import { UserDto } from '../models/user'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class EventService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getEvent(eventId : number, neighborhoodId : number): Observable<Event> {
        const eventDto$ = this.http.get<EventDto>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${eventId}`);

        return eventDto$.pipe(
            mergeMap((eventDto: EventDto) => {
                return forkJoin([
                    this.http.get<NeighborhoodDto>(eventDto.neighborhood),
                    this.http.get<UserDto[]>(eventDto.attendees)
                ]).pipe(
                    map(([neighborhood, attendees]) => {
                        return {
                            eventId: eventDto.eventId,
                            name: eventDto.name,
                            description: eventDto.description,
                            date: eventDto.date,
                            neighborhood: neighborhood,
                            startTime: eventDto.startTime,
                            endTime: eventDto.endTime,
                            attendees: attendees,
                            self: eventDto.self
                        } as Event;
                    })
                );
            })
        );
    }

    public getEventsByDate(neighborhoodId : number, date : Date): Observable<Event[]> {
        const params = new HttpParams().set('date', date.toString())

        return this.http.get<EventDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events`, { params }).pipe(
            mergeMap((eventsDto: EventDto[]) => {
                const eventObservables = eventsDto.map(eventDto =>
                    forkJoin([
                        this.http.get<NeighborhoodDto>(eventDto.neighborhood),
                        this.http.get<UserDto[]>(eventDto.attendees)
                    ]).pipe(
                        map(([neighborhood, attendees]) => {
                            return {
                                eventId: eventDto.eventId,
                                name: eventDto.name,
                                description: eventDto.description,
                                date: eventDto.date,
                                neighborhood: neighborhood,
                                startTime: eventDto.startTime,
                                endTime: eventDto.endTime,
                                attendees: attendees,
                                self: eventDto.self
                            } as Event;
                        })
                    )
                );

                 return forkJoin(eventObservables);
            })
        );
    }

    public addEvent(event: EventForm, neighborhoodId : number): Observable<EventForm> {
        return this.http.post<EventForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events`, event)
    }

    public updateEvent(event: EventForm, neighborhoodId : number): Observable<EventForm> {
        return this.http.patch<EventForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${event.eventId}`, event)
    }

    public deleteEvent(eventId: number, neighborhoodId : number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${eventId}`)
    }
}
