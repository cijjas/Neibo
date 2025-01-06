import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { Affiliation, mapWorker, parseLinkHeader, WorkerDto, NeighborhoodDto, AffiliationDto, WorkerRoleDto, NeighborhoodService, mapNeighborhood, LinkKey } from '@shared/index';
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
        queryParams: {
            page?: number;
            size?: number;
            inNeighborhood?: string;
            forWorker?: string;
        } = {}
    ): Observable<{ affiliations: Affiliation[]; totalPages: number; currentPage: number }> {

        let affiliationsUrl: string = this.linkService.getLink(LinkKey.AFFILIATIONS);

        let params = new HttpParams();

        if (queryParams.inNeighborhood) params = params.set('inNeighborhood', queryParams.inNeighborhood);
        if (queryParams.forWorker) params = params.set('forWorker', queryParams.forWorker);
        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http
            .get<AffiliationDto[]>(affiliationsUrl, { params, observe: 'response' })
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

    public createAffiliation(
        neighborhoodUrl: string,
    ): Observable<string | null> {
        let affiliationsUrl: string = this.linkService.getLink(LinkKey.AFFILIATIONS);
        let unverifiedWorkerRoleUrl: string = this.linkService.getLink(LinkKey.UNVERIFIED_WORKER_ROLE);
        let workerUrl: string = this.linkService.getLink(LinkKey.USER_WORKER);

        const body = {
            worker: workerUrl,
            neighborhood: neighborhoodUrl,
            workerRole: unverifiedWorkerRoleUrl
        };

        return this.http.post(affiliationsUrl, body, { observe: 'response' }).pipe(
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
                console.error('Error creating Affiliation for worker', workerUrl, error);
                return of(null);
            })
        )
    }
    /**
     * Creates multiple affiliations by calling createAffiliation() for each neighborhoodUrl.
     * @param neighborhoodUrls A list of neighborhood (amenity) URLs
     * @returns An observable that emits an array of string | null (location headers or null)
     */
    public createAffiliations(neighborhoodUrls: string[]): Observable<(string | null)[]> {
        if (!neighborhoodUrls || neighborhoodUrls.length === 0) {
            // No URLs to process; return an empty array right away
            return of([]);
        }

        // Map each URL to your single-affiliation creation request
        const affiliationRequests: Observable<string | null>[] = neighborhoodUrls.map((url) =>
            this.createAffiliation(url) // your existing function
        );

        // Use forkJoin to run them in parallel
        return forkJoin(affiliationRequests);
    }

    public verifyWorker(workerUrl: string): Observable<Affiliation> {
        const affiliationsUrl: string = this.linkService.getLink(LinkKey.AFFILIATIONS);
        const neighborhoodUrl: string = this.linkService.getLink(LinkKey.NEIGHBORHOOD_SELF);
        const verifiedWorkerRoleUrl: string = this.linkService.getLink(LinkKey.VERIFIED_WORKER_ROLE);

        let params = new HttpParams()
            .set('inNeighborhood', neighborhoodUrl)
            .set('forWorker', workerUrl);

        return this.http.patch<AffiliationDto>(affiliationsUrl, { workerRole: verifiedWorkerRoleUrl }, { params }).pipe(
            mergeMap((newAffiliation) => mapAffiliation(this.http, newAffiliation))
        );
    }

    public rejectWorker(workerUrl: string): Observable<Affiliation> {
        const affiliationsUrl: string = this.linkService.getLink(LinkKey.AFFILIATIONS);
        const neighborhoodUrl: string = this.linkService.getLink(LinkKey.NEIGHBORHOOD_SELF);
        const rejectedWorkerRoleUrl: string = this.linkService.getLink(LinkKey.REJECTED_WORKER_ROLE);

        let params = new HttpParams()
            .set('inNeighborhood', neighborhoodUrl)
            .set('forWorker', workerUrl);

        return this.http.patch<AffiliationDto>(affiliationsUrl, { workerRole: rejectedWorkerRoleUrl }, { params }).pipe(
            mergeMap((newAffiliation) => mapAffiliation(this.http, newAffiliation))
        );
    }

    public deleteAffiliation(
        neighborhoodUrl: string
    ): Observable<void> {
        const affiliationsUrl: string = this.linkService.getLink(LinkKey.AFFILIATIONS);
        let workerUrl: string = this.linkService.getLink(LinkKey.USER_WORKER);

        let params = new HttpParams();

        params = params.set("forWorker", workerUrl);
        params = params.set("inNeighborhood", neighborhoodUrl);

        return this.http.delete<void>(affiliationsUrl, { params });
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
            neighborhood: mapNeighborhood(neighborhoodDto),
            self: affiliationDto._links.self,
        }))
    );
}
