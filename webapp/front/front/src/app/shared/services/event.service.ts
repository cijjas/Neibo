import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
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
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService,
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }


    public getEvent(event : string): Observable<Event> {
        const eventDto$ = this.http.get<EventDto>(event, { headers: this.headers });

        return eventDto$.pipe(
            mergeMap((eventDto: EventDto) => {
                return forkJoin([
                    this.http.get<NeighborhoodDto>(eventDto._links.neighborhood),
                    this.http.get<UserDto[]>(eventDto._links.attendees)
                ]).pipe(
                    map(([neighborhood, attendees]) => {
                        return {

                            name: eventDto.name,
                            description: eventDto.description,
                            date: eventDto.date,
                            neighborhood: neighborhood,
                            startTime: eventDto.startTime,
                            endTime: eventDto.endTime,
                            attendees: attendees,
                            self: eventDto._links.self
                        } as Event;
                    })
                );
            })
        );
    }

    public getEventsByDate(neighborhood : string, date : Date, page : number, size : number ): Observable<Event[]> {
        let params = new HttpParams();

        if(date) params = params.set('forDate', date.toString())
        if(page) params = params.set('page', page.toString())
        if(size) params = params.set('size', size.toString())

        return this.http.get<EventDto[]>(`${neighborhood}/events`, { params, headers: this.headers }).pipe(
            mergeMap((eventsDto: EventDto[]) => {
                const eventObservables = eventsDto.map(eventDto =>
                    forkJoin([
                        this.http.get<NeighborhoodDto>(eventDto._links.neighborhood),
                        this.http.get<UserDto[]>(eventDto._links.attendees)
                    ]).pipe(
                        map(([neighborhood, attendees]) => {
                            return {
                                name: eventDto.name,
                                description: eventDto.description,
                                date: eventDto.date,
                                neighborhood: neighborhood,
                                startTime: eventDto.startTime,
                                endTime: eventDto.endTime,
                                attendees: attendees,
                                self: eventDto._links.self
                            } as Event;
                        })
                    )
                );

                 return forkJoin(eventObservables);
            })
        );
    }

    public addEvent(event: EventForm, neighborhood: string): Observable<EventForm> {
        return this.http.post<EventForm>(`${neighborhood}/events`, event, { headers: this.headers })
    }

    public updateEvent(eventForm: EventForm, event: string): Observable<EventForm> {
        return this.http.patch<EventForm>(event, eventForm, { headers: this.headers })
    }

    public deleteEvent(event: string): Observable<void> {
        return this.http.delete<void>(event, { headers: this.headers })
    }
}
