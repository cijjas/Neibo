import { HttpClient, HttpParams } from '@angular/common/http'
import { Neighborhood, NeighborhoodDto, NeighborhoodForm } from '../models/neighborhood'
import { User, UserDto } from "../models/user"
import { ContactDto } from "../models/contact"
import { ResourceDto } from "../models/resource"
import { Channel } from "../models/channel"
import { WorkerDto } from "../models/worker"
import { EventDto } from "../models/event"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class NeighborhoodService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getNeighborhood(neighborhoodId: number): Observable<Neighborhood> {
        const neighborhoodDto$ = this.http.get<NeighborhoodDto>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}`);

        return neighborhoodDto$.pipe(
            mergeMap((neighborhoodDto: NeighborhoodDto) => {
                return forkJoin([
                    this.http.get<UserDto[]>(neighborhoodDto.users),
                    this.http.get<ContactDto[]>(neighborhoodDto.contacts),
                    this.http.get<EventDto[]>(neighborhoodDto.events),
                    this.http.get<ResourceDto[]>(neighborhoodDto.resources),
                    this.http.get<Channel[]>(neighborhoodDto.channels),
                    this.http.get<WorkerDto[]>(neighborhoodDto.workers)
                ]).pipe(
                    map(([users, contacts, events, resources, channels, workers]) => {
                        return {
                            neighborhoodId: neighborhoodDto.neighborhoodId,
                            name: neighborhoodDto.name,
                            users: users,
                            contacts: contacts,
                            events: events,
                            resources: resources,
                            channels: channels,
                            workers: workers,
                            self: neighborhoodDto.self
                        } as Neighborhood;
                    })
                );
            })
        );
    }

    public getNeighborhoods(page: number, size: number): Observable<Neighborhood[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());

        return this.http.get<NeighborhoodDto[]>(`${this.apiServerUrl}/neighborhoods/`, { params }).pipe(
            mergeMap((neighborhoods: NeighborhoodDto[]) => {
                const neighborhoodObservables = neighborhoods.map(neighborhoodDto =>
                    forkJoin([
                        this.http.get<UserDto[]>(neighborhoodDto.users),
                    this.http.get<ContactDto[]>(neighborhoodDto.contacts),
                    this.http.get<EventDto[]>(neighborhoodDto.events),
                    this.http.get<ResourceDto[]>(neighborhoodDto.resources),
                    this.http.get<Channel[]>(neighborhoodDto.channels),
                    this.http.get<WorkerDto[]>(neighborhoodDto.workers),
                    ]).pipe(
                        map(([users, contacts, events, resources, channels, workers]) => {
                            return {
                                neighborhoodId: neighborhoodDto.neighborhoodId,
                                name: neighborhoodDto.name,
                                users: users,
                                contacts: contacts,
                                events: events,
                                resources: resources,
                                channels: channels,
                                workers: workers,
                                self: neighborhoodDto.self
                            } as Neighborhood;
                        })
                    )
                );

                 return forkJoin(neighborhoodObservables);
            })
        );
    }

    public addNeighborhood(neighborhood: NeighborhoodForm): Observable<NeighborhoodForm> {
        return this.http.post<NeighborhoodForm>(`${this.apiServerUrl}/neighborhoods`, neighborhood)
    }
}
