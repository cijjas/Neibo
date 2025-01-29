import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { HateoasLinksService } from "@core/index";
import { Language, LinkKey, LanguageDto } from "@shared/index";

@Injectable({ providedIn: "root" })
export class LanguageService {
  constructor(
    private http: HttpClient,
    private linkService: HateoasLinksService
  ) {}

  public getLanguage(url: string): Observable<Language> {
    return this.http.get<LanguageDto>(url).pipe(map(mapLanguage));
  }

  public getLanguages(): Observable<Language[]> {
    let url = this.linkService.getLink(LinkKey.LANGUAGES);
    return this.http
      .get<LanguageDto[]>(url)
      .pipe(map((languageDtos) => languageDtos.map(mapLanguage)));
  }
}

export function mapLanguage(languageDto: LanguageDto): Language {
  return {
    name: languageDto.name,
    displayName: formatLanguageName(languageDto.name),
    self: languageDto._links.self,
  };
}

export function formatLanguageName(name: string): string {
  return name.charAt(0).toUpperCase() + name.slice(1).toLowerCase();
}
