import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Product } from '../../models/index';
import { ProductDto, UserDto, DepartmentDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';

@Injectable({ providedIn: 'root' })
export class ProductService {
    constructor(private http: HttpClient) { }

    public getProduct(url: string): Observable<Product> {
        return this.http.get<ProductDto>(url).pipe(
            mergeMap((productDto: ProductDto) => mapProduct(this.http, productDto))
        );
    }

    public listProducts(url: string, page: number, size: number): Observable<Product[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<ProductDto[]>(url, { params }).pipe(
            mergeMap((productsDto: ProductDto[]) => {
                const productObservables = productsDto.map((productDto) =>
                    mapProduct(this.http, productDto)
                );
                return forkJoin(productObservables);
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
                remainingUnits: productDto.remainingUnits,
                creationDate: productDto.creationDate,
                firstImage: productDto.firstProductImage,
                secondImage: productDto.secondProductImage,
                thirdImage: productDto.thirdProductImage,
                seller: seller,
                department: department.name,
                self: productDto._links.self
            } as Product;
        })
    );
}
