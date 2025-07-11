import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { map, mergeMap, timeout, toArray } from 'rxjs/operators';
import {
  EventDto,
  Event,
  parseLinkHeader,
  LinkKey,
  formatTime,
  extractCountFromHeaders,
  Attendance,
} from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { from } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { formatDate } from 'date-fns/format';
import { enUS, es } from 'date-fns/locale';

@Injectable({ providedIn: 'root' })
export class EventService {
  constructor(
    private http: HttpClient,
    private linkService: HateoasLinksService,
  ) { }

  public getEvent(url: string): Observable<Event> {
    return this.http
      .get<EventDto>(url)
      .pipe(mergeMap((eventDto: EventDto) => mapEvent(this.http, eventDto)));
  }

  public getEvents(
    url: string,
    queryParams: {
      page?: number;
      size?: number;
      forDate?: string;
    } = {},
  ): Observable<{ events: Event[]; totalPages: number; currentPage: number }> {
    let params = new HttpParams();

    if (queryParams.page !== undefined)
      params = params.set('page', queryParams.page.toString());
    if (queryParams.size !== undefined)
      params = params.set('size', queryParams.size.toString());
    if (queryParams.forDate)
      params = params.set('forDate', queryParams.forDate);

    return this.http.get<EventDto[]>(url, { params, observe: 'response' }).pipe(
      timeout(10000), // Set a 10-second timeout for the HTTP request
      mergeMap(response => {
        const eventsDto: EventDto[] = response.body || [];
        const pagination = parseLinkHeader(response.headers.get('Link'));

        const eventObservables = eventsDto.map(eventDto =>
          mapEvent(this.http, eventDto),
        );

        return forkJoin(eventObservables).pipe(
          map(events => ({
            events,
            totalPages: pagination.totalPages,
            currentPage: pagination.currentPage,
          })),
        );
      }),
    );
  }

  public getEventsForDateRange(
    url: string,
    dates: string[],
  ): Observable<Event[]> {
    return from(dates).pipe(
      mergeMap(
        date =>
          this.getEvents(url, { forDate: date }).pipe(
            map(response => response.events),
            catchError(error => {
              console.error(`Error fetching events for date ${date}:`, error);
              return of([]);
            }),
          ),
        5,
      ),
      toArray(),
      map(eventsArrays => eventsArrays.flat()),
    );
  }

  public createEvent(
    name: string,
    description: string,
    eventDate: Date,
    startTime: string,
    endTime: string,
  ): Observable<string | null> {
    const body: EventDto = {
      name: name,
      description: description,
      eventDate: eventDate,
      startTime: startTime,
      endTime: endTime,
    };

    let eventsUrl: string = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_EVENTS,
    );

    return this.http.post(eventsUrl, body, { observe: 'response' }).pipe(
      map(response => {
        const locationHeader = response.headers.get('Location');
        if (locationHeader) {
          return locationHeader;
        } else {
          console.error('Location header not found:');
          return null;
        }
      }),
      catchError(error => {
        console.error('Error creating event', name, error);
        return of(null);
      }),
    );
  }

  public deleteEvent(eventUrl: string): Observable<void> {
    return this.http.delete<void>(eventUrl);
  }
}

export function mapEvent(
  http: HttpClient,
  eventDto: EventDto,
): Observable<Event> {
  return forkJoin([
    http.get<Attendance[]>(eventDto._links.attendanceUsers, { observe: 'response' }),
  ]).pipe(
    map(([attendance]) => {
      const attendanceCount = extractCountFromHeaders(attendance.headers);
      const userLang = localStorage.getItem('language');
      const locale = userLang === 'es' ? es : enUS;
      return {
        name: eventDto.name,
        description: eventDto.description,
        eventDate: eventDto.eventDate,
        eventDateDisplay: formatDate(eventDto.eventDate, 'EE, dd MMM yyyy', {
          locale,
        }),
        startTime: formatTime(eventDto.startTime),
        endTime: formatTime(eventDto.endTime),
        duration: calculateDurationInMinutes(
          eventDto.startTime,
          eventDto.endTime,
        ),
        attendees: eventDto._links.attendanceUsers,
        attendeesCount: attendanceCount,
        self: eventDto._links.self,
      } as Event;
    }),
  );
}

function calculateDurationInMinutes(
  startTime: string,
  endTime: string,
): number {
  const [startHours, startMinutes] = startTime.split(':').map(Number);
  const [endHours, endMinutes] = endTime.split(':').map(Number);

  const startDate = new Date(0, 0, 0, startHours, startMinutes);
  const endDate = new Date(0, 0, 0, endHours, endMinutes);

  const durationMilliseconds = endDate.getTime() - startDate.getTime();
  return durationMilliseconds / (1000 * 60);
}
