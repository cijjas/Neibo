import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Affiliation } from '../../model/index';
import { WorkerDto, NeighborhoodDto, AffiliationDto } from '../../dtos/app-dtos';
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

    public listAffiliations(url: string, page: number, size: number): Observable<Affiliation[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

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
        http.get<NeighborhoodDto>(affiliationDto._links.neighborhood)
    ]).pipe(
        map(([worker, neighborhoodDto]) => {
            return {
                worker: worker,
                workerRole: affiliationDto.workerRole,
                neighborhoodName: neighborhoodDto.name,
                self: affiliationDto._links.self
            } as Affiliation;
        })
    );
}
