import { HttpClient, HttpParams } from '@angular/common/http';
import {Purchase} from './purchase'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class PostService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getPurchases(neighborhoodId: number, userId: number, type: string, page: number, size: number): Observable<Purchase[]> {
        const params = new HttpParams().set('type', type).set('page', page.toString()).set('size', size.toString());
    
        return this.http.get<Purchase[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/users/${userId}/transactions`, { params });
    }
}