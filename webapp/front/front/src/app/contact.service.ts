import { HttpClient, HttpParams } from '@angular/common/http'
import { ContactForm } from './contactForm'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class ContactService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getContacts(neighborhoodId : number): Observable<ContactForm[]> {
        return this.http.get<ContactForm[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/contacts`)
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
