import { HttpClient, HttpParams } from '@angular/common/http'
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

    constructor(private http: HttpClient) { }

    public getPurchases(neighborhoodId: number, userId: number, transactionType: string, page: number, size: number): Observable<Purchase[]> {
        const params = new HttpParams().set('transactionType', transactionType).set('page', page.toString()).set('size', size.toString())

        return this.http.get<PurchaseDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/users/${userId}/transactions`, { params }).pipe(
            mergeMap((purchasesDto: PurchaseDto[]) => {
                const purchaseObservables = purchasesDto.map(purchaseDto =>
                    forkJoin([
                        this.http.get<ProductDto>(purchaseDto.product),
                        this.http.get<UserDto>(purchaseDto.user)
                    ]).pipe(
                        map(([product, user]) => {
                            return {
                                purchaseId: purchaseDto.purchaseId,
                                units: purchaseDto.units,
                                purchaseDate: purchaseDto.purchaseDate,
                                product: product,
                                user: user,
                                self: purchaseDto.self
                            } as Purchase;
                        })
                    )
                );

                 return forkJoin(purchaseObservables);
            })
        );
    }
}
