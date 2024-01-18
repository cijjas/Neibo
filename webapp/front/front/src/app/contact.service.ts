import { HttpClient, HttpParams } from '@angular/common/http'
import { Contact } from './contact'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class ContactService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getContacts(neighborhoodId : number): Observable<Contact[]> {
        return this.http.get<Contact[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/contacts`)
    }

    public addContact(contact: Contact, neighborhoodId : number): Observable<Contact> {
        return this.http.post<Contact>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/contacts`, contact)
    }

    public updateContact(contact: Contact, neighborhoodId : number): Observable<Contact> {
        return this.http.patch<Contact>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/contacts/${contact.contactId}`, contact)
    }

    public deleteContact(contactId: number, neighborhoodId : number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/contacts/${contactId}`)
    }
}