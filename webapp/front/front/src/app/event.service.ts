import { HttpClient, HttpParams } from '@angular/common/http';
import { Event } from './event'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class EventService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getEvent(eventId : number, neighborhoodId : number): Observable<Event> {
        return this.http.get<Event>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${eventId}`)
    }

    public getEventsByDate(neighborhoodId : number, date : Date): Observable<Event[]> {
        const params = new HttpParams().set('date', date.toString());
        return this.http.get<Event[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events`)
    }

    public addEvent(event: Event, neighborhoodId : number): Observable<Event> {
        return this.http.post<Event>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events`, event)
    }

    public updateEvent(event: Event, neighborhoodId : number): Observable<Event> {
        return this.http.patch<Event>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${event.eventId}`, event)
    }

    public deleteEvent(eventId: number, neighborhoodId : number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/events/${eventId}`)
    }
}