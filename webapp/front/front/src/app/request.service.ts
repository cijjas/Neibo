import { HttpClient, HttpParams } from '@angular/common/http'
import { RequestForm } from './requestForm'
import { Request } from './request'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class RequestService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getRequests(neighborhoodId: number, userId: number, productId: number, page: number, size: number): Observable<Request[]> {
        const params = new HttpParams().set('userId', userId.toString()).set('productId', productId.toString()).set('page', page.toString()).set('size', size.toString());

        return this.http.get<Request[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/requests`, { params })
    }

    public addRequest(neighborhoodId: number, productId: number, request: RequestForm): Observable<RequestForm> {
        const params = new HttpParams().set('productId', productId.toString())
        return this.http.post<RequestForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/requests`, request, { params })
    }

}
