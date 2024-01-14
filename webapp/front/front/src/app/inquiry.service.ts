import { HttpClient, HttpParams } from '@angular/common/http';
import { Inquiry } from './inquiry'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class InquiryService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getInquiries(neighborhoodId : number, productId : number, page : number, size : number): Observable<Inquiry[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
        return this.http.get<Inquiry[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products/${productId}/inquiries`)
    }

    public addInquiry(inquiry: Inquiry, neighborhoodId : number, productId : number): Observable<Inquiry> {
        return this.http.post<Inquiry>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products/${productId}/inquiries`, inquiry)
    }

    public updateInquiry(inquiry: Inquiry, neighborhoodId : number, productId : number): Observable<Inquiry> {
        return this.http.patch<Inquiry>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products/${productId}/inquiries/${inquiry.inquiryId}`, inquiry)
    }

}