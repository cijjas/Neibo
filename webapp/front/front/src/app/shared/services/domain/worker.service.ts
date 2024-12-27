import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { Worker, WorkerDto, UserDto, NeighborhoodDto, ProfessionDto, ImageDto, ReviewsAverageDto, mapUser, parseLinkHeader, mapProfession, ReviewsCountDto, PostsCountDto } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class WorkerService {

    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService,
    ) { }

    public getWorker(url: string): Observable<Worker> {
        return this.http.get<WorkerDto>(url).pipe(
            mergeMap((workerDto: WorkerDto) => mapWorker(this.http, workerDto))
        );
    }

    public getWorkers(
        queryParams: {
            page?: number;
            size?: number;
            withProfessions?: string[];
            inNeighborhoods?: string[];
            withRole?: string;
            withStatus?: string;
        } = {}
    ): Observable<{ workers: Worker[]; totalPages: number; currentPage: number }> {
        let workersUrl: string = this.linkService.getLink('neighborhood:workers')

        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.withProfessions && queryParams.withProfessions.length > 0) params = params.set('withProfessions', queryParams.withProfessions.join(','));
        if (queryParams.inNeighborhoods && queryParams.inNeighborhoods.length > 0) params = params.set('inNeighborhoods', queryParams.inNeighborhoods.join(','));
        if (queryParams.withRole) params = params.set('withRole', queryParams.withRole);
        if (queryParams.withStatus) params = params.set('withStatus', queryParams.withStatus);

        return this.http
            .get<WorkerDto[]>(workersUrl, { params, observe: 'response' })
            .pipe(
                mergeMap((response) => {
                    // Handle 204 No Content response
                    if (response.status === 204 || !response.body) {
                        return of({
                            workers: [],
                            totalPages: 0,
                            currentPage: 0,
                        });
                    }

                    const workersDto = response.body || [];
                    const linkHeader = response.headers.get('Link');
                    const paginationInfo = parseLinkHeader(linkHeader);

                    const workerObservables = workersDto.map((workerDto) => mapWorker(this.http, workerDto));

                    return forkJoin(workerObservables).pipe(
                        map((workers) => ({
                            workers,
                            totalPages: paginationInfo.totalPages,
                            currentPage: paginationInfo.currentPage,
                        }))
                    );
                })
            );
    }

}

export function mapWorker(http: HttpClient, workerDto: WorkerDto): Observable<Worker> {
    return forkJoin([
        http.get<UserDto>(workerDto._links.user).pipe(mergeMap(userDto => mapUser(http, userDto))),
        http.get<NeighborhoodDto[]>(workerDto._links.workerNeighborhoods),
        http.get<ProfessionDto[]>(workerDto._links.professions),
        http.get<ReviewsAverageDto>(workerDto._links.reviewsAverage),
        http.get<ReviewsCountDto>(workerDto._links.reviewsCount),
        http.get<PostsCountDto>(workerDto._links.postsCount),
    ]).pipe(
        map(([user, neighborhoods, professions, reviewAverage, reviewsCount, postsCount]) => {
            return {
                phoneNumber: workerDto.phoneNumber,
                businessName: workerDto.businessName,
                address: workerDto.address,
                bio: workerDto.bio,
                averageRating: reviewAverage.average,
                reviews: workerDto._links.reviews,
                totalReviews: reviewsCount.count,
                posts: workerDto._links.posts,
                totalPosts: postsCount.count,
                user: user,
                backgroundImage: workerDto._links.backgroundImage,
                neighborhoodAffiliated: neighborhoods.map((n) => n.name),
                professions: professions.map((p) => mapProfession(p)),
                self: workerDto._links.self
            } as Worker;
        })
    );
}
