import { HttpClient, HttpParams } from '@angular/common/http'
import { InquiryForm } from './inquiryForm'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class InquiryService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getInquiries(neighborhoodId : number, productId : number, page : number, size : number): Observable<InquiryForm[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString())
        return this.http.get<InquiryForm[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products/${productId}/inquiries`)
    }

    public addInquiry(inquiry: InquiryForm, neighborhoodId : number, productId : number): Observable<InquiryForm> {
        return this.http.post<InquiryForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products/${productId}/inquiries`, inquiry)
    }

    public updateInquiry(inquiry: InquiryForm, neighborhoodId : number, productId : number): Observable<InquiryForm> {
        return this.http.patch<InquiryForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/products/${productId}/inquiries/${inquiry.inquiryId}`, inquiry)
    }

}
