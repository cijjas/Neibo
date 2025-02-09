import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, of, throwError } from 'rxjs';
import { catchError, map, mergeMap, switchMap, tap } from 'rxjs/operators';
import {
  Worker,
  WorkerDto,
  UserDto,
  NeighborhoodDto,
  ProfessionDto,
  ReviewsAverageDto,
  mapUser,
  parseLinkHeader,
  mapProfession,
  ReviewsCountDto,
  PostsCountDto,
  UserService,
  LinkKey,
} from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class WorkerService {
  constructor(
    private http: HttpClient,
    private linkService: HateoasLinksService,
    private userService: UserService,
  ) {}

  public getWorker(url: string): Observable<Worker> {
    return this.http
      .get<WorkerDto>(url)
      .pipe(
        mergeMap((workerDto: WorkerDto) => mapWorker(this.http, workerDto)),
      );
  }

  public getWorkers(
    queryParams: {
      page?: number;
      size?: number;
      withProfession?: string[]; // Array of professions
      inNeighborhood?: string[];
      withRole?: string;
      withStatus?: string;
    } = {},
  ): Observable<{
    workers: Worker[];
    totalPages: number;
    currentPage: number;
  }> {
    let workersUrl: string = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_WORKERS,
    );

    let params = new HttpParams();

    if (queryParams.page !== undefined)
      params = params.set('page', queryParams.page.toString());
    if (queryParams.size !== undefined)
      params = params.set('size', queryParams.size.toString());
    if (queryParams.inNeighborhood && queryParams.inNeighborhood.length > 0)
      params = params.set(
        'inNeighborhood',
        queryParams.inNeighborhood.join(','),
      );
    if (queryParams.withRole)
      params = params.set('withRole', queryParams.withRole);
    if (queryParams.withStatus)
      params = params.set('withStatus', queryParams.withStatus);

    // Add each profession as a separate query parameter
    if (queryParams.withProfession && queryParams.withProfession.length > 0) {
      queryParams.withProfession.forEach(profession => {
        params = params.append('withProfession', profession);
      });
    }

    return this.http
      .get<WorkerDto[]>(workersUrl, { params, observe: 'response' })
      .pipe(
        mergeMap(response => {
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

          const workerObservables = workersDto.map(workerDto =>
            mapWorker(this.http, workerDto),
          );

          return forkJoin(workerObservables).pipe(
            map(workers => ({
              workers,
              totalPages: paginationInfo.totalPages || 0,
              currentPage: paginationInfo.currentPage || 0,
            })),
          );
        }),
        catchError(error => {
          console.error('Error fetching workers:', error);
          return of({
            workers: [],
            totalPages: 0,
            currentPage: 0,
          });
        }),
      );
  }

  public createWorker(
    name: string,
    surname: string,
    password: string,
    mail: string,
    language: string,
    identification: number,
    professions: string[],
    phoneNumber: string,
    businessName: string,
    address: string,
  ): Observable<string | null> {
    const workersUrl: string = this.linkService.getLink(LinkKey.WORKERS);

    return this.userService
      .createWorkerUser(name, surname, password, mail, language, identification)
      .pipe(
        switchMap(createdUserUrl => {
          if (!createdUserUrl) {
            console.error('User creation failed.');
            return of(null);
          }

          const body: WorkerDto = {
            user: createdUserUrl,
            professions: professions,
            phoneNumber: phoneNumber,
            businessName: businessName,
            address: address,
          };

          return this.http.post(workersUrl, body, { observe: 'response' }).pipe(
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
              console.error('Error creating Worker', error);
              return of(null);
            }),
          );
        }),
        catchError(error => {
          console.error('Error creating User', error);
          return of(null);
        }),
      );
  }

  public updateWorker(worker: WorkerDto): Observable<void> {
    let workerUrl: string = this.linkService.getLink(LinkKey.USER_WORKER);
    return this.http.patch<void>(workerUrl, worker).pipe(
      catchError(error => {
        console.error('Error updating product:', error);
        return throwError(() => new Error('Failed to update worker.'));
      }),
    );
  }
}
export function mapWorker(
  http: HttpClient,
  workerDto: WorkerDto,
): Observable<Worker> {
  const user$ = http
    .get<UserDto>(workerDto._links.user)
    .pipe(mergeMap(userDto => mapUser(http, userDto)));

  const neighborhoods$ = http.get<NeighborhoodDto[]>(
    workerDto._links.workerNeighborhoods,
  );
  const professions$ = http.get<ProfessionDto[]>(workerDto._links.professions);
  const reviewsAverage$ = http.get<ReviewsAverageDto>(
    workerDto._links.reviewsAverage,
  );
  const reviewsCount$ = http.get<ReviewsCountDto>(
    workerDto._links.reviewsCount,
  );
  const postsCount$ = http.get<PostsCountDto>(workerDto._links.postsCount);

  return forkJoin([
    user$,
    neighborhoods$,
    professions$,
    reviewsAverage$,
    reviewsCount$,
    postsCount$,
  ]).pipe(
    map(
      ([
        user,
        neighborhoods,
        professions,
        reviewAverage,
        reviewsCount,
        postsCount,
      ]) => {
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
          neighborhoodAffiliated: neighborhoods
            ? neighborhoods.map(n => n.name)
            : null,
          professions: professions
            ? professions.map(p => mapProfession(p))
            : null,
          self: workerDto._links.self,
        } as Worker;
      },
    ),
  );
}
