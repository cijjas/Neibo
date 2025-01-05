import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HateoasLinksService } from '@core/index';
import { LanguageDto } from '@shared/dtos/app-dtos';
import { Language } from '@shared/models';


@Injectable({ providedIn: 'root' })
export class LanguageService {
    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService
    ) { }

    public getLanguage(url: string): Observable<Language> {
        return this.http.get<LanguageDto>(url).pipe(
            map(mapLanguage)
        );
    }

    public getLanguages(): Observable<Language[]> {
        let params = new HttpParams();

        let url = this.linkService.getLink('root:languages');
        url = 'http://localhost:8080/languages';
        return this.http.get<LanguageDto[]>(url).pipe(
            map((languageDtos) => languageDtos.map(mapLanguage))
        );
    }
}

export function mapLanguage(languageDto: LanguageDto): Language {
    return {
        name: languageDto.name,
        displayName: formatLanguageName(languageDto.name),
        self: languageDto._links.self
    };
}

/**
 * Format a language name for display purposes.
 * @param name The language name to format.
 */
export function formatLanguageName(name: string): string {
    return name.charAt(0).toUpperCase() + name.slice(1).toLowerCase(); // Capitalize the first letter
}