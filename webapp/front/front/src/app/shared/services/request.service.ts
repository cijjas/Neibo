import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Request, RequestDto, RequestForm } from '../models/request'
import { ProductDto } from "../models/product"
import { UserDto } from "../models/user"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class RequestService {
    private apiServerUrl = environment.apiBaseUrl
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService,
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }

    public getRequests(neighborhood: string, user: string, product: string, productType: string, productStatus: string, page: number, size: number): Observable<Request[]> {
        let params = new HttpParams()
        
        if(user) params = params.set('requestedBy', user)
        if(product) params = params.set('forProduct', product)
        if(productType) params = params.set('withType', productType)
        if(productStatus) params = params.set('withStatus', productStatus)
        if(page) params = params.set('page', page.toString())
        if(size) params = params.set('size', size.toString());

        return this.http.get<RequestDto[]>(`/${neighborhood}/requests`, { params, headers: this.headers }).pipe(
            mergeMap((requestsDto: RequestDto[]) => {
                const requestObservables = requestsDto.map(requestDto =>
                    forkJoin([
                        this.http.get<ProductDto>(requestDto._links.product),
                        this.http.get<UserDto>(requestDto._links.user)
                    ]).pipe(
                        map(([product, user]) => {
                            return {
                                message: requestDto.message,
                                requestDate: requestDto.requestDate,
                                fulfilled: requestDto.fulfilled,
                                product: product,
                                user: user,
                                self: requestDto._links.self
                            } as Request;
                        })
                    )
                );

                 return forkJoin(requestObservables);
            })
        );
    }

    public addRequest(neighborhood: string, product: string, request: RequestForm): Observable<RequestForm> {
        return this.http.post<RequestForm>(`${neighborhood}/requests`, request, { headers: this.headers })
    }

}
