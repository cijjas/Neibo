import { HttpClient, HttpParams } from '@angular/common/http'
import { Product, ProductDto, ProductForm } from './product'
import { UserDto } from "./user"
import { InquiryDto } from "./inquiry"
import { Department } from "./department"
import { RequestDto } from "./request"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class ProductService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getProduct(neighborhoodId: number, productId: number): Observable<Product> {
        const productDto$ = this.http.get<ProductDto>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products/${productId}`);
            
        return productDto$.pipe(
            mergeMap((productDto: ProductDto) => {
                return forkJoin([
                    this.http.get<UserDto>(productDto.seller),
                    this.http.get<Department>(productDto.department),
                    this.http.get<InquiryDto[]>(productDto.inquiries),
                    this.http.get<RequestDto[]>(productDto.requests)
                ]).pipe(
                    map(([seller, department, inquiries, requests]) => {
                        return {
                            productId: productDto.productId,
                            name: productDto.name,
                            description: productDto.description,
                            price: productDto.price,
                            used: productDto.used,
                            remainingUnits: productDto.remainingUnits,
                            primaryPicture: productDto.primaryPicture,
                            secondaryPicture: productDto.secondaryPicture,
                            tertiaryPicture: productDto.tertiaryPicture,
                            seller: seller,
                            department: department,
                            inquiries: inquiries,
                            requests: requests,
                            self: productDto.self
                        } as Product;
                    })
                );
            })
        );
    }

    public getProducts(neighborhoodId: number, department: string, userId: number, productStatus: string, page: number, size: number): Observable<Product[]> {
        const params = new HttpParams().set('department', department).set('userId', userId.toString()).set('productStatus', productStatus).set('page', page.toString()).set('size', size.toString());

        return this.http.get<ProductDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products`, { params }).pipe(
            mergeMap((productsDto: ProductDto[]) => {
                const productObservables = productsDto.map(productDto => 
                    forkJoin([
                        this.http.get<UserDto>(productDto.seller),
                        this.http.get<Department>(productDto.department),
                        this.http.get<InquiryDto[]>(productDto.inquiries),
                        this.http.get<RequestDto[]>(productDto.requests)
                    ]).pipe(
                        map(([seller, department, inquiries, requests]) => {
                            return {
                                productId: productDto.productId,
                                name: productDto.name,
                                description: productDto.description,
                                price: productDto.price,
                                used: productDto.used,
                                remainingUnits: productDto.remainingUnits,
                                primaryPicture: productDto.primaryPicture,
                                secondaryPicture: productDto.secondaryPicture,
                                tertiaryPicture: productDto.tertiaryPicture,
                                seller: seller,
                                department: department,
                                inquiries: inquiries,
                                requests: requests,
                                self: productDto.self
                            } as Product;
                        })
                    )
                );
        
                 return forkJoin(productObservables);
            })
        );
    }

    public addProduct(neighborhoodId: number, product: ProductForm): Observable<ProductForm> {
        return this.http.post<ProductForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products`, product)
    }

    public updateProduct(neighborhoodId: number, product: ProductForm): Observable<ProductForm> {
        return this.http.patch<ProductForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products/${product.productId}`, product)
    }

    public deleteProduct(neighborhoodId: number, productId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products/${productId}`)
    }

}
