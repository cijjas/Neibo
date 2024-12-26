import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Contact, ContactDto, parseLinkHeader } from '@shared/index';

@Injectable({ providedIn: 'root' })
export class ContactService {
    constructor(private http: HttpClient) { }

    public getContact(url: string): Observable<Contact> {
        return this.http.get<ContactDto>(url).pipe(
            map((contactDto: ContactDto) => mapContact(contactDto))
        );
    }

    public getContacts(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
        } = {}
    ): Observable<{ contacts: Contact[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http.get<ContactDto[]>(url, { params, observe: 'response' }).pipe(
            map((response) => {
                const contactsDto: ContactDto[] = response.body || [];
                const pagination = parseLinkHeader(response.headers.get('Link'));

                const contacts = contactsDto.map(mapContact);

                return {
                    contacts,
                    totalPages: pagination.totalPages,
                    currentPage: pagination.currentPage
                };
            })
        );
    }
}

export function mapContact(contactDto: ContactDto): Contact {
    return {
        name: contactDto.name,
        address: contactDto.address,
        phoneNumber: contactDto.phone,
        self: contactDto._links.self
    };
}
