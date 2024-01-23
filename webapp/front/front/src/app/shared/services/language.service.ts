import { HttpClient, HttpParams } from '@angular/common/http'
import { Language } from '../models/language'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'

@Injectable({providedIn: 'root'})
export class LanguageService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getLanguage(languageId : number): Observable<Language> {
        return this.http.get<Language>(`${this.apiServerUrl}/${languageId}`)
    }

    public getLanguages(): Observable<Language[]> {
        return this.http.get<Language[]>(`${this.apiServerUrl}/languages`)
    }

}
