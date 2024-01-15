import { HttpClient, HttpParams } from '@angular/common/http';
import {Time} from './time'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class TimeService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getTimes(): Observable<Time[]> {    
        return this.http.get<Time[]>(`${this.apiServerUrl}/times`);
    }

    public getTime(timeId: number): Observable<Time> {    
        return this.http.get<Time>(`${this.apiServerUrl}/times/${timeId}`);
    }

}