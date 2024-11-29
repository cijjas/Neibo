import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Worker } from '../../models/index';
import { WorkerDto, UserDto, NeighborhoodDto, ProfessionDto, ImageDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';

@Injectable({ providedIn: 'root' })
export class WorkerService {

    constructor(
        private http: HttpClient
    ) { }

    public getWorker(url: string): Observable<Worker> {
        return this.http.get<WorkerDto>(url).pipe(
            mergeMap((workerDto: WorkerDto) => mapWorker(this.http, workerDto))
        );
    }

    public getWorkers(url: string, page: number, size: number): Observable<Worker[]> {
        let params = new HttpParams();
        // QP withProfessions=professionUrlList
        // QP inNeighborhoods=neighborhoodUrlList
        // QP withRole=workerRoleUrl
        // QP withStatus=workerStatusUrl

        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<WorkerDto[]>(url, { params }).pipe(
            mergeMap((workersDto: WorkerDto[]) => {
                const workerObservables = workersDto.map(workerDto => mapWorker(this.http, workerDto));
                return forkJoin(workerObservables);
            })
        );
    }
}

export function mapWorker(http: HttpClient, workerDto: WorkerDto): Observable<Worker> {
    return forkJoin([
        http.get<UserDto>(workerDto._links.user).pipe(mergeMap(userDto => mapUser(http, userDto))),
        http.get<NeighborhoodDto[]>(workerDto._links.neighborhoods),
        http.get<ProfessionDto[]>(workerDto._links.professions),
    ]).pipe(
        map(([user, neighborhoods, professions]) => {
            return {
                phoneNumber: workerDto.phoneNumber,
                businessName: workerDto.businessName,
                address: workerDto.address,
                bio: workerDto.bio,
                averageRating: workerDto.averageRating,
                user: user,
                backgroundImage: workerDto._links.backgroundImage,
                neighborhoodAffiliated: neighborhoods.map((n) => n.name),
                professions: professions.map((p) => p.name),
                self: workerDto._links.self
            } as Worker;
        })
    );
}
