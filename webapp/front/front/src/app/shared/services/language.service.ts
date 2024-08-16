import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Language, LanguageDto } from '../models/language'
import { Observable, map } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'

@Injectable({providedIn: 'root'})
export class LanguageService {
    private apiServerUrl = environment.apiBaseUrl
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService,
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }

    public getLanguage(language : string): Observable<Language> {
        const languageDto$ = this.http.get<LanguageDto>(language, { headers: this.headers })

        return languageDto$.pipe(
            map((languageDto: LanguageDto) => {
                return {
                    language: languageDto.language,
                    self: languageDto._links.self
                } as Language;
            })
        );
    }

    public getLanguages(): Observable<Language[]> {
        return this.http.get<LanguageDto[]>(`${this.apiServerUrl}/languages`, { headers: this.headers }).pipe(
            map((languageDtos: LanguageDto[]) => {
                return languageDtos.map((languageDto: LanguageDto) => {
                    return {
                        language: languageDto.language,
                        self: languageDto._links.self
                    } as Language;
                })
            })
        );
    }

}
