import { HttpClient } from '@angular/common/http'
import { Profession } from '../models/profession'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'

@Injectable({providedIn: 'root'})
export class ProfessionService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getProfessions(): Observable<Profession[]> {
        return this.http.get<Profession[]>(`${this.apiServerUrl}/professions`)
    }

    public getProfession(professionId: number): Observable<Profession> {
        return this.http.get<Profession>(`${this.apiServerUrl}/professions/${professionId}`)
    }
}
