import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Review } from '../model/review';
import { ReviewDto, UserDto } from '../dtos/app-dtos';
import { mapUser } from './user.service';

@Injectable({ providedIn: 'root' })
export class ReviewService {
    constructor(private http: HttpClient) {}

    public getReview(url: string): Observable<Review> {
        return this.http.get<ReviewDto>(url).pipe(
            mergeMap((reviewDto: ReviewDto) => mapReview(this.http, reviewDto))
        );
    }

    public listReviews(url: string, page: number, size: number): Observable<Review[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<ReviewDto[]>(url, { params }).pipe(
            mergeMap((reviewsDto: ReviewDto[]) => {
                const reviewObservables = reviewsDto.map((reviewDto) =>
                    mapReview(this.http, reviewDto)
                );
                return forkJoin(reviewObservables);
            })
        );
    }
}

export function mapReview(http: HttpClient, reviewDto: ReviewDto): Observable<Review> {
    return forkJoin([
        http.get<UserDto>(reviewDto._links.user).pipe(mergeMap(userDto => mapUser(http, userDto)))
    ]).pipe(
        map(([user]) => {
            return {
                rating: reviewDto.rating,
                review: reviewDto.review,
                date: reviewDto.date,
                user: user,
                self: reviewDto._links.self
            } as Review;
        })
    );
}

