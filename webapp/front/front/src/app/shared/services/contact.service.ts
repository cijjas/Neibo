import { HttpClient, HttpParams } from '@angular/common/http'
import { Contact, ContactDto, ContactForm } from '../models/contact'
import { NeighborhoodDto } from '../models/neighborhood'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class ContactService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getContacts(neighborhoodId : number): Observable<Contact[]> {
        return this.http.get<ContactDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/contacts`).pipe(
            mergeMap((contactsDto: ContactDto[]) => {
                const contactObservables = contactsDto.map(contactDto =>
                    this.http.get<NeighborhoodDto>(contactDto.neighborhood).pipe(
                        map((neighborhood) => {
                            return {
                                contactId: contactDto.contactId,
                                contactName: contactDto.contactName,
                                contactAddress: contactDto.contactAddress,
                                contactPhone: contactDto.contactPhone,
                                neighborhood: neighborhood,
                                self: contactDto.self
                            } as Contact;
                        })
                    )
                );

                 return forkJoin(contactObservables);
            })
        );
    }

    public addContact(contact: ContactForm, neighborhoodId : number): Observable<ContactForm> {
        return this.http.post<ContactForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/contacts`, contact)
    }

    public updateContact(contact: ContactForm, neighborhoodId : number): Observable<ContactForm> {
        return this.http.patch<ContactForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/contacts/${contact.contactId}`, contact)
    }

    public deleteContact(contactId: number, neighborhoodId : number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/contacts/${contactId}`)
    }
}
