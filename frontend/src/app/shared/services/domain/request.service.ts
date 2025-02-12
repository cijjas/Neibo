import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, of, throwError } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import {
  Request,
  RequestDto,
  UserDto,
  RequestStatusDto,
  ProductDto,
  mapUser,
  parseLinkHeader,
  mapProduct,
  LinkKey,
} from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class RequestService {
  constructor(
    private http: HttpClient,
    private linkService: HateoasLinksService,
  ) {}

  public getRequest(url: string): Observable<Request> {
    return this.http
      .get<RequestDto>(url)
      .pipe(
        mergeMap((requestDto: RequestDto) => mapRequest(this.http, requestDto)),
      );
  }

  public getRequests(
    queryParams: {
      page?: number;
      size?: number;
      requestedBy?: string;
      forProduct?: string;
      withType?: string;
      withStatus?: string;
    } = {},
  ): Observable<{
    requests: Request[];
    totalPages: number;
    currentPage: number;
  }> {
    const url: string = this.linkService.getLink(LinkKey.NEIGHBORHOOD_REQUESTS);

    let params = new HttpParams();

    if (queryParams.page !== undefined)
      params = params.set('page', queryParams.page.toString());
    if (queryParams.size !== undefined)
      params = params.set('size', queryParams.size.toString());
    if (queryParams.requestedBy)
      params = params.set('requestedBy', queryParams.requestedBy);
    if (queryParams.forProduct)
      params = params.set('forProduct', queryParams.forProduct);
    if (queryParams.withType)
      params = params.set('withType', queryParams.withType);
    if (queryParams.withStatus)
      params = params.set('withStatus', queryParams.withStatus);

    if (
      (queryParams.requestedBy && !queryParams.withType) ||
      (!queryParams.requestedBy && queryParams.withType)
    ) {
      throw new Error(
        'Both `requestedBy` and `withType` must be provided together, or neither of them.',
      );
    }

    return this.http
      .get<RequestDto[]>(url, { params, observe: 'response' })
      .pipe(
        mergeMap(response => {
          const requestsDto: RequestDto[] = response.body || [];
          const pagination = parseLinkHeader(response.headers.get('Link'));

          if (requestsDto.length === 0) {
            return of({
              requests: [],
              totalPages: pagination.totalPages,
              currentPage: pagination.currentPage,
            });
          }

          const requestObservables = requestsDto.map(requestDto =>
            mapRequest(this.http, requestDto),
          );
          return forkJoin(requestObservables).pipe(
            map(requests => ({
              requests,
              totalPages: pagination.totalPages,
              currentPage: pagination.currentPage,
            })),
          );
        }),
      );
  }

  public createRequest(
    message: string,
    unitsRequested: number,
    product: string,
    user: string,
  ): Observable<string> {
    const url: string = this.linkService.getLink(LinkKey.NEIGHBORHOOD_REQUESTS);
    return this.http
      .post(
        url,
        { message, unitsRequested, product, user },
        { observe: 'response' },
      )
      .pipe(
        map(response => {
          const location = response.headers.get('Location');
          if (!location) {
            throw new Error('Location header not found in response');
          }
          return location;
        }),
      );
  }

  public updateRequest(requestUrl: string, requestData: any): Observable<void> {
    return this.http.patch<void>(requestUrl, requestData).pipe(
      catchError(error => {
        console.error('Error updating request:', error);
        return throwError(() => new Error('Failed to update request.'));
      }),
    );
  }
}

export function mapRequest(
  http: HttpClient,
  requestDto: RequestDto,
): Observable<Request> {
  return forkJoin([
    http
      .get<UserDto>(requestDto._links.requestUser)
      .pipe(mergeMap(userDto => mapUser(http, userDto))),
    http
      .get<ProductDto>(requestDto._links.product)
      .pipe(mergeMap(productDto => mapProduct(http, productDto))),
    http.get<RequestStatusDto>(requestDto._links.requestStatus),
  ]).pipe(
    map(([requestingUser, product, requestStatusDto]) => {
      return {
        message: requestDto.message,
        unitsRequested: requestDto.unitsRequested,
        createdAt: requestDto.requestDate,
        fulfilledAt: requestDto.purchaseDate,
        requestStatus: requestStatusDto.status,
        requestingUser: requestingUser,
        product: product,
        self: requestDto._links.self,
      } as Request;
    }),
  );
}
