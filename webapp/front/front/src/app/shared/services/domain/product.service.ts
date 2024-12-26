import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, of, throwError } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { mapDepartment, parseLinkHeader, mapUser, ProductDto, UserDto, DepartmentDto, Product } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class ProductService {
    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService,
    ) { }

    public getProduct(url: string): Observable<Product> {
        return this.http.get<ProductDto>(url).pipe(
            mergeMap((productDto: ProductDto) => mapProduct(this.http, productDto))
        );
    }

    public getProducts(
        queryParams: {
            page?: number;
            size?: number;
            inDepartment?: string;
            forUser?: string;
            withStatus?: string;
        } = {}
    ): Observable<{ products: Product[]; totalPages: number; currentPage: number }> {
        let url: string = this.linkService.getLink('neighborhood:products')

        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.inDepartment) params = params.set('inDepartment', queryParams.inDepartment);
        if (queryParams.forUser) params = params.set('forUser', queryParams.forUser);
        if (queryParams.withStatus) params = params.set('withStatus', queryParams.withStatus);

        return this.http
            .get<ProductDto[]>(url, { params, observe: 'response' })
            .pipe(
                mergeMap((response) => {
                    if (response.status === 204 || !response.body) {
                        return of({
                            products: [],
                            totalPages: 0,
                            currentPage: 0,
                        });
                    }

                    const productsDto = response.body || [];
                    const linkHeader = response.headers.get('Link');
                    const paginationInfo = parseLinkHeader(linkHeader);

                    const productObservables = productsDto.map((productDto) => mapProduct(this.http, productDto));

                    return forkJoin(productObservables).pipe(
                        map((products) => ({
                            products,
                            totalPages: paginationInfo.totalPages,
                            currentPage: paginationInfo.currentPage,
                        }))
                    );
                })
            );
    }

    public createProduct(
        productData: {
            name: string;
            description: string;
            price: string;
            used: string;
            department: string;
            remainingUnits: string;
            images: string[];
            user: string;
        }
    ): Observable<string> {
        const { images, ...productDetails } = productData;

        if (images.length > 3) {
            throw new Error('A product can have up to 3 images.');
        }

        const payload = {
            ...productDetails,
            images,
        };

        return this.http.post(this.linkService.getLink('neighborhood:products'), payload, {
            observe: 'response'
        }).pipe(
            map(response => {
                const locationHeader = response.headers.get('Location');
                if (!locationHeader) {
                    throw new Error('Location header not found in response.');
                }
                return locationHeader;
            }),
            catchError(error => {
                console.error('Error creating product:', error);
                return throwError(() => new Error('Failed to create product.'));
            })
        );
    }

    public updateProduct(productUrl: string, productData: any): Observable<void> {
        return this.http.patch<void>(productUrl, productData).pipe(
            catchError(error => {
                console.error('Error updating product:', error);
                return throwError(() => new Error('Failed to update product.'));
            })
        );
    }
}

export function mapProduct(http: HttpClient, productDto: ProductDto): Observable<Product> {
    return forkJoin([
        http.get<UserDto>(productDto._links.productUser).pipe(mergeMap(userDto => mapUser(http, userDto))),
        http.get<DepartmentDto>(productDto._links.department)
    ]).pipe(
        map(([seller, department]) => {
            return {
                name: productDto.name,
                description: productDto.description,
                price: productDto.price,
                used: productDto.used,
                stock: productDto.remainingUnits,
                inquiries: productDto._links.inquiries,
                createdAt: productDto.creationDate,
                firstImage: productDto._links.firstProductImage,
                secondImage: productDto._links.secondProductImage,
                thirdImage: productDto._links.thirdProductImage,
                seller: seller,
                department: mapDepartment(department),
                self: productDto._links.self
            } as Product;
        })
    );
}
