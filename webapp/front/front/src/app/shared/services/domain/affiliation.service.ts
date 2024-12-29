import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Affiliation, mapWorker, parseLinkHeader, WorkerDto, NeighborhoodDto, AffiliationDto, WorkerRoleDto } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class AffiliationService {
    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService,
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
    ): Observable<{ affiliations: Affiliation[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.inNeighborhood) params = params.set('inNeighborhood', queryParams.inNeighborhood);
        if (queryParams.forWorker) params = params.set('forWorker', queryParams.forWorker);
        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http
            .get<AffiliationDto[]>(url, { params, observe: 'response' })
            .pipe(
                mergeMap((response: HttpResponse<AffiliationDto[]>) => {
                    const affiliationsDto = response.body || [];
                    const linkHeader = response.headers.get('Link');
                    const paginationInfo = parseLinkHeader(linkHeader);

                    const affiliationObservables = affiliationsDto.map((affiliationDto) =>
                        mapAffiliation(this.http, affiliationDto)
                    );

                    return forkJoin(affiliationObservables).pipe(
                        map((affiliations) => ({
                            affiliations,
                            totalPages: paginationInfo.totalPages,
                            currentPage: paginationInfo.currentPage,
                        }))
                    );
                })
            );
    }

    public verifyWorker(workerUrl: string): Observable<Affiliation> {
        const affiliationsUrl: string = this.linkService.getLink('neighborhood:affiliations');
        const neighborhoodUrl: string = this.linkService.getLink('neighborhood:self');
        const verifiedWorkerRoleUrl: string = this.linkService.getLink('neighborhood:verifiedWorkerRole');

        let params = new HttpParams()
            .set('inNeighborhood', neighborhoodUrl)
            .set('forWorker', workerUrl);

        return this.http.patch<AffiliationDto>(affiliationsUrl, { workerRole: verifiedWorkerRoleUrl }, { params }).pipe(
            mergeMap((newAffiliation) => mapAffiliation(this.http, newAffiliation))
        );
    }

    public rejectWorker(workerUrl: string): Observable<Affiliation> {
        const affiliationsUrl: string = this.linkService.getLink('neighborhood:affiliations');
        const neighborhoodUrl: string = this.linkService.getLink('neighborhood:self');
        const rejectedWorkerRoleUrl: string = this.linkService.getLink('neighborhood:rejectedWorkerRole');

        let params = new HttpParams()
            .set('inNeighborhood', neighborhoodUrl)
            .set('forWorker', workerUrl);

        return this.http.patch<AffiliationDto>(affiliationsUrl, { workerRole: rejectedWorkerRoleUrl }, { params }).pipe(
            mergeMap((newAffiliation) => mapAffiliation(this.http, newAffiliation))
        );
    }
}

export function mapAffiliation(http: HttpClient, affiliationDto: AffiliationDto): Observable<Affiliation> {
    return forkJoin([
        http.get<WorkerDto>(affiliationDto._links.worker).pipe(mergeMap((workerDto) => mapWorker(http, workerDto))),
        http.get<NeighborhoodDto>(affiliationDto._links.neighborhood),
        http.get<WorkerRoleDto>(affiliationDto._links.workerRole),
    ]).pipe(
        map(([worker, neighborhoodDto, workerRoleDto]) => ({
            worker: worker,
            role: workerRoleDto.role,
            neighborhoodName: neighborhoodDto.name,
            self: affiliationDto._links.self,
        }))
    );
}
