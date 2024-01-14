import { HttpClient, HttpParams } from '@angular/common/http';
import { Day } from './day'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class DayService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getDay(dayId : number): Observable<Day> {
        return this.http.get<Day>(`${this.apiServerUrl}/days/${dayId}`)
    }

    public getDays(): Observable<Day[]> {
        return this.http.get<Day[]>(`${this.apiServerUrl}/days`)
    }

}