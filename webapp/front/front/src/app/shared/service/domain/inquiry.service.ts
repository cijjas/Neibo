import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Inquiry } from '../../model/index';
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

    public listInquiries(url: string, page: number, size: number): Observable<Inquiry[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<InquiryDto[]>(url, { params }).pipe(
            mergeMap((inquiriesDto: InquiryDto[]) => {
                const inquiryObservables = inquiriesDto.map((inquiryDto) =>
                    mapInquiry(this.http, inquiryDto)
                );
                return forkJoin(inquiryObservables);
            })
        );
    }
}

export function mapInquiry(http: HttpClient, inquiryDto: InquiryDto): Observable<Inquiry> {
    return forkJoin([
        http.get<UserDto>(inquiryDto._links.user).pipe(mergeMap(userDto => mapUser(http, userDto))),
        http.get<UserDto>(inquiryDto._links.replier).pipe(mergeMap(userDto => mapUser(http, userDto)))
    ]).pipe(
        map(([inquirer, replier]) => {
            return {
                message: inquiryDto.message,
                reply: inquiryDto.reply,
                date: inquiryDto.date,
                inquirer: inquirer,
                replier: replier,
                self: inquiryDto._links.self
            } as Inquiry;
        })
    );
}
