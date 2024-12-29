import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Contact, ContactDto, parseLinkHeader } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class ContactService {
    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService
    ) { }

    public getContact(url: string): Observable<Contact> {
        return this.http.get<ContactDto>(url).pipe(
            map((contactDto: ContactDto) => mapContact(contactDto))
        );
    }

    public getContacts(
        queryParams: {
            page?: number;
            size?: number;
        } = {}
    ): Observable<{ contacts: Contact[]; totalPages: number; currentPage: number }> {
        let contactsUrl: string = this.linkService.getLink('neighborhood:contacts')
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http.get<ContactDto[]>(contactsUrl, { params, observe: 'response' }).pipe(
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

    public createContact(
        name: string,
        phone: string,
        address: string
    ): Observable<string | null> {

        const body: ContactDto = {
            name: name,
            phone: phone,
            address: address
        };

        let contactsUrl: string = this.linkService.getLink('neighborhood:contacts')

        return this.http.post(contactsUrl, body, { observe: 'response' }).pipe(
            map(response => {
                const locationHeader = response.headers.get('Location');
                if (locationHeader) {
                    return locationHeader;
                } else {
                    console.error('Location header not found:');
                    return null;
                }
            }),
            catchError(error => {
                console.error('Error creating contact', name, error);
                return of(null);
            })
        )
    }

    public deleteContact(contactUrl: string): Observable<void> {
        return this.http.delete<void>(contactUrl);
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
