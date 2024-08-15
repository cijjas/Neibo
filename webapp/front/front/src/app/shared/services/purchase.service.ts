import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Purchase, PurchaseDto } from '../models/purchase'
import { UserDto } from "../models/user"
import { Product, ProductDto } from "../models/product"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class PostService {
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

    public getPurchases(user: string, transactionType: string, page: number, size: number): Observable<Purchase[]> {
        let params = new HttpParams()
        
        if(transactionType) params = params.set('withType', transactionType)
        if(page) params = params.set('page', page.toString())
        if(size) params = params.set('size', size.toString())

        return this.http.get<PurchaseDto[]>(`${user}/transactions`, { params, headers: this.headers }).pipe(
            mergeMap((purchasesDto: PurchaseDto[]) => {
                const purchaseObservables = purchasesDto.map(purchaseDto =>
                    forkJoin([
                        this.http.get<ProductDto>(purchaseDto._links.product),
                        this.http.get<UserDto>(purchaseDto._links.user)
                    ]).pipe(
                        map(([product, user]) => {
                            return {
                                units: purchaseDto.units,
                                purchaseDate: purchaseDto.purchaseDate,
                                product: product,
                                user: user,
                                self: purchaseDto._links.self
                            } as Purchase;
                        })
                    )
                );

                 return forkJoin(purchaseObservables);
            })
        );
    }
}
