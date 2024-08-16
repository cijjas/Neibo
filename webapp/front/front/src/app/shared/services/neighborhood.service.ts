import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Neighborhood, NeighborhoodDto, NeighborhoodForm } from '../models/neighborhood'
import { User, UserDto } from "../models/user"
import { ContactDto } from "../models/contact"
import { ResourceDto } from "../models/resource"
import { Channel, ChannelDto } from "../models/channel"
import { WorkerDto } from "../models/worker"
import { EventDto } from "../models/event"
import { Observable, forkJoin, tap } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class NeighborhoodService {
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

    public getNeighborhood(neighborhood: string): Observable<Neighborhood> {
        const neighborhoodDto$ = this.http.get<NeighborhoodDto>(neighborhood, { headers: this.headers });

        return neighborhoodDto$.pipe(
            mergeMap((neighborhoodDto: NeighborhoodDto) => {
                return forkJoin([
                    this.http.get<UserDto[]>(neighborhoodDto._links.users),
                    this.http.get<ContactDto[]>(neighborhoodDto._links.contacts),
                    this.http.get<EventDto[]>(neighborhoodDto._links.events),
                    this.http.get<ResourceDto[]>(neighborhoodDto._links.resources),
                    this.http.get<ChannelDto[]>(neighborhoodDto._links.channels),
                    this.http.get<WorkerDto[]>(neighborhoodDto._links.workers)
                ]).pipe(
                    map(([users, contacts, events, resources, channels, workers]) => {
                        return {
                            name: neighborhoodDto.name,
                            users: users,
                            contacts: contacts,
                            events: events,
                            resources: resources,
                            channels: channels,
                            workers: workers,
                            self: neighborhoodDto._links.self
                        } as Neighborhood;
                    })
                );
            })
        );
    }

    public getNeighborhoods(page: number, size: number, worker: string): Observable<Neighborhood[]> {
        let params = new HttpParams()

        if(page) params = params.set('page', page.toString())
        if(size) params = params.set('size', size.toString())
        if(worker) params = params.set('withWorker', worker)

        return this.http.get<NeighborhoodDto[]>(`${this.apiServerUrl}/neighborhoods`, { params, headers: this.headers }).pipe(
          mergeMap((neighborhoods: NeighborhoodDto[]) => {
                const neighborhoodObservables = neighborhoods.map(neighborhoodDto =>
                    forkJoin([
                        this.http.get<UserDto[]>(neighborhoodDto._links.users),
                        this.http.get<ContactDto[]>(neighborhoodDto._links.contacts),
                        this.http.get<EventDto[]>(neighborhoodDto._links.events),
                        this.http.get<ResourceDto[]>(neighborhoodDto._links.resources),
                        this.http.get<ChannelDto[]>(neighborhoodDto._links.channels),
                        this.http.get<WorkerDto[]>(neighborhoodDto._links.workers),
                    ]).pipe(
                        map(([users, contacts, events, resources, channels, workers]) => {
                          return {
                                name: neighborhoodDto.name,
                                users: users,
                                contacts: contacts,
                                events: events,
                                resources: resources,
                                channels: channels,
                                workers: workers,
                                self: neighborhoodDto._links.self
                            } as Neighborhood;
                        })
                    )
                );

                 return forkJoin(neighborhoodObservables);
            })
        );

    }


    public addNeighborhood(neighborhood: NeighborhoodForm): Observable<NeighborhoodForm> {
        return this.http.post<NeighborhoodForm>(`${this.apiServerUrl}/neighborhoods`, neighborhood, { headers: this.headers })
    }
}
