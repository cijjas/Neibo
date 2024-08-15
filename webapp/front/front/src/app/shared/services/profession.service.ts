import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Profession, ProfessionDto } from '../models/profession'
import { Observable, map } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'

@Injectable({providedIn: 'root'})
export class ProfessionService {
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

    public getProfessions(): Observable<Profession[]> {
        return this.http.get<ProfessionDto[]>(`${this.apiServerUrl}/professions`, { headers: this.headers }).pipe(
            map((professionDtos: ProfessionDto[]) => {
                return professionDtos.map((professionDto: ProfessionDto) => {
                    return {
                        profession: professionDto.profession,
                        self: professionDto._links.self
                    } as Profession;
                });
            })
        );        
    }

    public getProfession(profession: string): Observable<Profession> {
        const professionDto$ = this.http.get<ProfessionDto>(profession, { headers: this.headers })

        return professionDto$.pipe(
            map((professionDto: ProfessionDto) => {
                return {
                    profession: professionDto.profession,
                    self: professionDto._links.self
                } as Profession;
            })
        );
    }
}
