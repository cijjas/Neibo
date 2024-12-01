import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Review } from '../../models/index';
import { ReviewDto, UserDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';
import { parseLinkHeader } from './utils';

@Injectable({ providedIn: 'root' })
export class ReviewService {
    constructor(private http: HttpClient) { }

    public getReview(url: string): Observable<Review> {
        return this.http.get<ReviewDto>(url).pipe(
            mergeMap((reviewDto: ReviewDto) => mapReview(this.http, reviewDto))
        );
    }

    public getReviews(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
        } = {}
    ): Observable<{ reviews: Review[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http
            .get<ReviewDto[]>(url, { params, observe: 'response' })
            .pipe(
                mergeMap((response) => {
                    const reviewsDto = response.body || [];
                    const linkHeader = response.headers.get('Link');
                    const paginationInfo = parseLinkHeader(linkHeader);

                    const reviewObservables = reviewsDto.map((reviewDto) => mapReview(this.http, reviewDto));

                    return forkJoin(reviewObservables).pipe(
                        map((reviews) => ({
                            reviews,
                            totalPages: paginationInfo.totalPages,
                            currentPage: paginationInfo.currentPage,
                        }))
                    );
                })
            );
    }

}

export function mapReview(http: HttpClient, reviewDto: ReviewDto): Observable<Review> {
    return forkJoin([
        http.get<UserDto>(reviewDto._links.reviewUser).pipe(mergeMap(userDto => mapUser(http, userDto)))
    ]).pipe(
        map(([user]) => {
            return {
                rating: reviewDto.rating,
                message: reviewDto.message,
                createdAt: reviewDto.creationDate,
                user: user,
                self: reviewDto._links.self
            } as Review;
        })
    );
}
