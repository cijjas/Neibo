import { HttpClient, HttpParams } from '@angular/common/http'
import { Product, ProductDto, ProductForm } from './product'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class ProductService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getProducts(neighborhoodId: number, department: string, userId: number, productStatus: string, page: number, size: number): Observable<Product[]> {
        const params = new HttpParams().set('department', department).set('userId', userId.toString()).set('productStatus', productStatus).set('page', page.toString()).set('size', size.toString());

        return this.http.get<Product[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products`, { params })
    }

    public getProduct(neighborhoodId: number, productId: number): Observable<Product> {
        return this.http.get<Product>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products/${productId}`)
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
