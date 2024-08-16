import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http'
import { Contact, ContactDto, ContactForm } from '../models/contact'
import { NeighborhoodDto } from '../models/neighborhood'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'
import { LoggedInService } from './loggedIn.service'

@Injectable({providedIn: 'root'})
export class ContactService {
    private apiServerUrl = environment.apiBaseUrl
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }

    public getContacts(neighborhood : number): Observable<Contact[]> {
        return this.http.get<ContactDto[]>(`${neighborhood}/contacts`, { headers: this.headers }).pipe(
            mergeMap((contactsDto: ContactDto[]) => {
                const contactObservables = contactsDto.map(contactDto =>
                    this.http.get<NeighborhoodDto>(contactDto._links.neighborhood).pipe(
                        map((neighborhood) => {
                            return {
                                contactName: contactDto.contactName,
                                contactAddress: contactDto.contactAddress,
                                contactPhone: contactDto.contactPhone,
                                neighborhood: neighborhood,
                                self: contactDto._links.self
                            } as Contact;
                        })
                    )
                );

                 return forkJoin(contactObservables);
            })
        );
    }

    public addContact(contactForm: ContactForm, neighborhood: string): Observable<ContactForm> {
        return this.http.post<ContactForm>(`${neighborhood}/contacts`, contactForm, { headers: this.headers})
    }

    public updateContact(contactForm: ContactForm, contact: string): Observable<ContactForm> {
        return this.http.patch<ContactForm>(`${contact}`, contactForm, { headers: this.headers })
    }

    public deleteContact(contact: string): Observable<void> {
        return this.http.delete<void>(contact, { headers: this.headers })
    }
}
