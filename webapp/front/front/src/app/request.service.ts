import { HttpClient, HttpParams } from '@angular/common/http'
import { Request, RequestDto, RequestForm } from './request'
import { ProductDto } from "./product"
import { UserDto } from "./user"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class RequestService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getRequests(neighborhoodId: number, userId: number, productId: number, page: number, size: number): Observable<Request[]> {
        const params = new HttpParams().set('userId', userId.toString()).set('productId', productId.toString()).set('page', page.toString()).set('size', size.toString());
            
        return this.http.get<RequestDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/requests`, { params }).pipe(
            mergeMap((requestsDto: RequestDto[]) => {
                const requestObservables = requestsDto.map(requestDto => 
                    forkJoin([
                        this.http.get<ProductDto>(requestDto.product),
                        this.http.get<UserDto>(requestDto.user)
                    ]).pipe(
                        map(([product, user]) => {
                            return {
                                requestId: requestDto.requestId,
                                message: requestDto.message,
                                requestDate: requestDto.requestDate,
                                fulfilled: requestDto.fulfilled,
                                product: product,
                                user: user,
                                self: requestDto.self
                            } as Request;
                        })
                    )
                );
        
                 return forkJoin(requestObservables);
            })
        );
    }

    public addRequest(neighborhoodId: number, productId: number, request: RequestForm): Observable<RequestForm> {
        const params = new HttpParams().set('productId', productId.toString())
        return this.http.post<RequestForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/requests`, request, { params })
    }

}
