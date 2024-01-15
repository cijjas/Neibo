import { HttpClient, HttpParams } from '@angular/common/http';
import {Review} from './review'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class ReviewService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getReviews(workerId: number, page: number, size: number): Observable<Review[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    
        return this.http.get<Review[]>(`${this.apiServerUrl}/workers/${workerId}/reviews`, { params });
    }

    public getReview(workerId: number, reviewId: number): Observable<Review> {    
        return this.http.get<Review>(`${this.apiServerUrl}/workers/${workerId}/reviews/${reviewId}`);
    }

    public addPost(workerId: number, review: Review): Observable<Review> {
        return this.http.post<Review>(`${this.apiServerUrl}/workers/${workerId}/reviews`, review);
    }

}