import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Inquiry, InquiryDto, InquiryForm } from '../models/inquiry'
import { ProductDto } from '../models/product'
import { UserDto } from '../models/user'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class InquiryService {
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

    public getInquiries(product: string, page : number, size : number): Observable<Inquiry[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());

        return this.http.get<InquiryDto[]>(`${product}/inquiries`, { params, headers: this.headers }).pipe(
            mergeMap((inquiriesDto: InquiryDto[]) => {
                const inquiryObservables = inquiriesDto.map(inquiryDto =>
                    forkJoin([
                        this.http.get<ProductDto>(inquiryDto._links.product),
                        this.http.get<UserDto>(inquiryDto._links.user)
                    ]).pipe(
                        map(([product, user]) => {
                            return {
                                message: inquiryDto.message,
                                reply: inquiryDto.reply,
                                inquiryDate: inquiryDto.inquiryDate,
                                product: product,
                                user: user,
                                self: inquiryDto._links.self
                            } as Inquiry;
                        })
                    )
                );

                 return forkJoin(inquiryObservables);
            })
        );
    }

    public addInquiry(inquiry: InquiryForm, product: string): Observable<InquiryForm> {
        return this.http.post<InquiryForm>(`${product}/inquiries`, inquiry, { headers: this.headers })
    }

    public updateInquiry(inquiryForm: InquiryForm, inquiry: string): Observable<InquiryForm> {
        return this.http.patch<InquiryForm>(inquiry, inquiryForm, { headers: this.headers })
    }

}
