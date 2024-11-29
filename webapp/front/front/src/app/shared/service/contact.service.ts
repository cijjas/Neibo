import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Contact } from '../model/contact';
import { ContactDto } from '../dtos/app-dtos';

@Injectable({ providedIn: 'root' })
export class ContactService {
    constructor(private http: HttpClient) {}

    public getContact(url: string): Observable<Contact> {
        return this.http.get<ContactDto>(url).pipe(
            map((contactDto: ContactDto) => mapContact(contactDto))
        );
    }

    public listContacts(url: string, page: number, size: number): Observable<Contact[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<ContactDto[]>(url, { params }).pipe(
            map((contactsDto: ContactDto[]) => contactsDto.map(mapContact))
        );
    }
}

export function mapContact(contactDto: ContactDto): Contact {
    return {
        name: contactDto.name,
        address: contactDto.address,
        phone: contactDto.phone,
        self: contactDto._links.self
    };
}
