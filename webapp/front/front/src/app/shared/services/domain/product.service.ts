import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Product } from '../../models/index';
import { ProductDto, UserDto, DepartmentDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';
import { parseLinkHeader } from './utils';

@Injectable({ providedIn: 'root' })
export class ProductService {
    constructor(private http: HttpClient) { }

    public getProduct(url: string): Observable<Product> {
        return this.http.get<ProductDto>(url).pipe(
            mergeMap((productDto: ProductDto) => mapProduct(this.http, productDto))
        );
    }

    public getProducts(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
            inDepartment?: string;
            forUser?: string;
            withStatus?: string;
        } = {}
    ): Observable<{ products: Product[]; totalPages: number; currentPage: number }> {
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
                createdAt: productDto.creationDate,
                firstImage: productDto._links.firstProductImage,
                secondImage: productDto._links.secondProductImage,
                thirdImage: productDto._links.thirdProductImage,
                seller: seller,
                department: department.name,
                self: productDto._links.self
            } as Product;
        })
    );
}
