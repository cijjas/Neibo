import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Request } from '../../models/index';
import { RequestDto, UserDto, RequestStatusDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';

@Injectable({ providedIn: 'root' })
export class RequestService {
    constructor(private http: HttpClient) { }

    public getRequest(url: string): Observable<Request> {
        return this.http.get<RequestDto>(url).pipe(
            mergeMap((requestDto: RequestDto) => mapRequest(this.http, requestDto))
        );
    }

    public listRequests(url: string, page: number, size: number): Observable<Request[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<RequestDto[]>(url, { params }).pipe(
            mergeMap((requestsDto: RequestDto[]) => {
                const requestObservables = requestsDto.map((requestDto) =>
                    mapRequest(this.http, requestDto)
                );
                return forkJoin(requestObservables);
            })
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
                requestDate: requestDto.requestDate,
                purchaseDate: requestDto.purchaseDate,
                requestStatus: requestStatusDto.status,
                requestingUser: requestingUser,
                self: requestDto._links.self
            } as Request;
        })
    );
}
