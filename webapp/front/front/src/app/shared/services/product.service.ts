import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Product, ProductDto, ProductForm } from '../models/product'
import { UserDto } from "../models/user"
import { InquiryDto } from "../models/inquiry"
import { DepartmentDto } from "../models/department"
import { RequestDto } from "../models/request"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'
import { ImageDto } from '../models/image'

@Injectable({ providedIn: 'root' })
export class ProductService {
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

    public getProduct(product: string): Observable<Product> {
        const productDto$ = this.http.get<ProductDto>(product, { headers: this.headers });

        return productDto$.pipe(
            mergeMap((productDto: ProductDto) => {
                return forkJoin([
                    this.http.get<UserDto>(productDto._links.seller),
                    this.http.get<DepartmentDto>(productDto._links.department),
                    this.http.get<InquiryDto[]>(productDto._links.inquiries),
                    this.http.get<RequestDto[]>(productDto._links.requests),
                    this.http.get<ImageDto>(productDto._links.primaryPicture),
                    this.http.get<ImageDto>(productDto._links.secondaryPicture),
                    this.http.get<ImageDto>(productDto._links.tertiaryPicture)
                ]).pipe(
                    map(([seller, department, inquiries, requests, primaryPicture, secondaryPicture, tertiaryPicture]) => {
                        return {
                            name: productDto.name,
                            description: productDto.description,
                            price: productDto.price,
                            used: productDto.used,
                            remainingUnits: productDto.remainingUnits,
                            creationDate: productDto.date,
                            primaryPicture: primaryPicture,
                            secondaryPicture: secondaryPicture,
                            tertiaryPicture: tertiaryPicture,
                            seller: seller,
                            department: department,
                            inquiries: inquiries,
                            requests: requests,
                            self: productDto._links.self
                        } as Product;
                    })
                );
            })
        );
    }

    public getProducts(neighborhood: string, department: string, user: string, productStatus: string, page: number, size: number): Observable<Product[]> {
        let params = new HttpParams()

        if (department) params = params.set('inDeparment', department)
        if (user) params = params.set('listedBy', user)
        if (productStatus) params = params.set('withStatus', productStatus)
        if (page) params = params.set('page', page.toString())
        if (size) params = params.set('size', size.toString());

        return this.http.get<ProductDto[]>(`${neighborhood}/products`, { params, headers: this.headers }).pipe(
            mergeMap((productsDto: ProductDto[]) => {
                const productObservables = productsDto.map(productDto =>
                    forkJoin([
                        this.http.get<UserDto>(productDto._links.seller),
                        this.http.get<DepartmentDto>(productDto._links.department),
                        this.http.get<InquiryDto[]>(productDto._links.inquiries),
                        this.http.get<RequestDto[]>(productDto._links.requests),
                        this.http.get<ImageDto>(productDto._links.primaryPicture),
                        this.http.get<ImageDto>(productDto._links.secondaryPicture),
                        this.http.get<ImageDto>(productDto._links.tertiaryPicture)
                    ]).pipe(
                        map(([seller, department, inquiries, requests, primaryPicture, secondaryPicture, tertiaryPicture]) => {
                            return {
                                name: productDto.name,
                                description: productDto.description,
                                price: productDto.price,
                                used: productDto.used,
                                remainingUnits: productDto.remainingUnits,
                                creationDate: productDto.date,
                                primaryPicture: primaryPicture,
                                secondaryPicture: secondaryPicture,
                                tertiaryPicture: tertiaryPicture,
                                seller: seller,
                                department: department,
                                inquiries: inquiries,
                                requests: requests,
                                self: productDto._links.self
                            } as Product;
                        })
                    )
                );

                return forkJoin(productObservables);
            })
        );
    }

    public addProduct(neighborhood: string, product: ProductForm): Observable<ProductForm> {
        return this.http.post<ProductForm>(`${neighborhood}/products`, product, { headers: this.headers })
    }

    public updateProduct(product: string, productForm: ProductForm): Observable<ProductForm> {
        return this.http.patch<ProductForm>(product, productForm, { headers: this.headers })
    }

    public deleteProduct(product: string): Observable<void> {
        return this.http.delete<void>(product, { headers: this.headers })
    }

}
