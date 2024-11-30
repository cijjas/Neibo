import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Inquiry } from '../../models/index';
import { InquiryDto, UserDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';

@Injectable({ providedIn: 'root' })
export class InquiryService {
    constructor(private http: HttpClient) { }

    public getInquiry(url: string): Observable<Inquiry> {
        return this.http.get<InquiryDto>(url).pipe(
            mergeMap((inquiryDto: InquiryDto) => mapInquiry(this.http, inquiryDto))
        );
    }

    public getInquiries(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
        } = {}
    ): Observable<Inquiry[]> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http.get<InquiryDto[]>(url, { params }).pipe(
            mergeMap((inquiriesDto: InquiryDto[]) => {
                const inquiryObservables = inquiriesDto.map(inquiryDto => mapInquiry(this.http, inquiryDto));
                return forkJoin(inquiryObservables);
            })
        );
    }

}

export function mapInquiry(http: HttpClient, inquiryDto: InquiryDto): Observable<Inquiry> {
    return forkJoin([
        http.get<UserDto>(inquiryDto._links.inquiryUser).pipe(mergeMap(userDto => mapUser(http, userDto))),
        http.get<UserDto>(inquiryDto._links.replyUser).pipe(mergeMap(userDto => mapUser(http, userDto)))
    ]).pipe(
        map(([inquirer, replier]) => {
            return {
                inquiryMessage: inquiryDto.message,
                responseMessage: inquiryDto.reply,
                inquiryDate: inquiryDto.inquiryDate,
                inquiryUser: inquirer,
                responseUser: replier,
                self: inquiryDto._links.self
            } as Inquiry;
        })
    );
}
