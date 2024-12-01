import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Request } from '../../models/index';
import { RequestDto, UserDto, RequestStatusDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';
import { parseLinkHeader } from './utils';

@Injectable({ providedIn: 'root' })
export class RequestService {
    constructor(private http: HttpClient) { }

    public getRequest(url: string): Observable<Request> {
        return this.http.get<RequestDto>(url).pipe(
            mergeMap((requestDto: RequestDto) => mapRequest(this.http, requestDto))
        );
    }

    public getRequests(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
            requestBy?: string;
            forProduct?: string;
            withType?: string;
            withStatus?: string;
        } = {}
    ): Observable<{ requests: Request[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.requestBy) params = params.set('requestBy', queryParams.requestBy);
        if (queryParams.forProduct) params = params.set('forProduct', queryParams.forProduct);
        if (queryParams.withType) params = params.set('withType', queryParams.withType);
        if (queryParams.withStatus) params = params.set('withStatus', queryParams.withStatus);

        if ((queryParams.requestBy && !queryParams.withType) || (!queryParams.requestBy && queryParams.withType)) {
            throw new Error('Both `requestBy` and `withType` must be provided together, or neither of them.');
        }

        return this.http.get<RequestDto[]>(url, { params, observe: 'response' }).pipe(
            map((response) => {
                const requestsDto: RequestDto[] = response.body || [];
                const pagination = parseLinkHeader(response.headers.get('Link'));

                const requestObservables = requestsDto.map(requestDto => mapRequest(this.http, requestDto));
                return forkJoin(requestObservables).pipe(
                    map((requests) => ({
                        requests,
                        totalPages: pagination.totalPages,
                        currentPage: pagination.currentPage
                    }))
                );
            }),
            mergeMap(result => result) // Flatten the nested observable
        );
    }
}

export function mapRequest(http: HttpClient, requestDto: RequestDto): Observable<Request> {
    return forkJoin([
        http.get<UserDto>(requestDto._links.requestUser).pipe(mergeMap(userDto => mapUser(http, userDto))),
        http.get<RequestStatusDto>(requestDto._links.requestStatus)
    ]).pipe(
        map(([requestingUser, requestStatusDto]) => {
            return {
                message: requestDto.message,
                unitsRequested: requestDto.unitsRequested,
                createdAt: requestDto.requestDate,
                fulfilledAt: requestDto.purchaseDate,
                requestStatus: requestStatusDto.status,
                requestingUser: requestingUser,
                self: requestDto._links.self
            } as Request;
        })
    );
}