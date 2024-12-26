import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Inquiry, InquiryDto, parseLinkHeader } from '@shared/index';

@Injectable({ providedIn: 'root' })
export class InquiryService {
    constructor(private http: HttpClient) { }

    public getInquiry(url: string): Observable<Inquiry> {
        return this.http.get<InquiryDto>(url).pipe(
            map(mapInquiry)
        );
    }

    public getInquiries(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
        } = {}
    ): Observable<{ inquiries: Inquiry[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http.get<InquiryDto[]>(url, { params, observe: 'response' }).pipe(
            map((response) => {
                const inquiriesDto: InquiryDto[] = response.body || [];
                const pagination = parseLinkHeader(response.headers.get('Link'));

                const inquiries = inquiriesDto.map(mapInquiry);

                return {
                    inquiries,
                    totalPages: pagination.totalPages,
                    currentPage: pagination.currentPage
                };
            })
        );
    }

    public createInquiry(url: string, message: string, user: string): Observable<string | null> {
        return this.http.post<InquiryDto>(url, { message, user }, { observe: 'response' }).pipe(
            map(response => {
                const locationHeader = response.headers.get('Location');
                if (locationHeader) {
                    return locationHeader;
                } else {
                    console.error('Location header not found');
                    return null;
                }
            }),
            catchError(error => {
                console.error('Error creating inquiry:', error);
                return of(null);
            })
        );
    }

    public updateInquiry(inquiryUrl: string, inquiryData: any): Observable<void> {
        return this.http.patch<void>(inquiryUrl, inquiryData).pipe(
            catchError(error => {
                console.error('Error updating inquiry:', error);
                return throwError(() => new Error('Failed to update inquiry.'));
            })
        );
    }
}

export function mapInquiry(inquiryDto: InquiryDto): Inquiry {
    return {
        inquiryMessage: inquiryDto.message,
        responseMessage: inquiryDto.reply,
        inquiryDate: inquiryDto.inquiryDate,
        self: inquiryDto._links.self
    } as Inquiry;
}
