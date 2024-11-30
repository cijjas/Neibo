import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Affiliation } from '../../models/index';
import { WorkerDto, NeighborhoodDto, AffiliationDto, WorkerRoleDto } from '../../dtos/app-dtos';
import { mapWorker } from './worker.service';

@Injectable({ providedIn: 'root' })
export class AffiliationService {

    constructor(
        private http: HttpClient
    ) { }

    public getAffiliation(url: string): Observable<Affiliation> {
        return this.http.get<AffiliationDto>(url).pipe(
            mergeMap((affiliationDto: AffiliationDto) => mapAffiliation(this.http, affiliationDto))
        );
    }

    public getAffiliations(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
            inNeighborhood?: string;
            forWorker?: string;
        } = {}
    ): Observable<Affiliation[]> {
        let params = new HttpParams();

        if (queryParams.inNeighborhood) params = params.set('inNeighborhood', queryParams.inNeighborhood);
        if (queryParams.forWorker) params = params.set('forWorker', queryParams.forWorker);
        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http.get<AffiliationDto[]>(url, { params }).pipe(
            mergeMap((affiliationsDto: AffiliationDto[]) => {
                const affiliationObservables = affiliationsDto.map(affiliationDto => mapAffiliation(this.http, affiliationDto));
                return forkJoin(affiliationObservables);
            })
        );
    }
}

export function mapAffiliation(http: HttpClient, affiliationDto: AffiliationDto): Observable<Affiliation> {
    return forkJoin([
        http.get<WorkerDto>(affiliationDto._links.worker).pipe(mergeMap(workerDto => mapWorker(http, workerDto))),
        http.get<NeighborhoodDto>(affiliationDto._links.neighborhood),
        http.get<WorkerRoleDto>(affiliationDto._links.workerRole)
    ]).pipe(
        map(([worker, neighborhoodDto, workerRoleDto]) => {
            return {
                worker: worker,
                role: workerRoleDto.role,
                neighborhoodName: neighborhoodDto.name,
                self: affiliationDto._links.self
            } as Affiliation;
        })
    );
}
