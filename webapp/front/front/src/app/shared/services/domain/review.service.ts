import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Review, ReviewDto, parseLinkHeader } from '@shared/index';

@Injectable({ providedIn: 'root' })
export class ReviewService {
    constructor(private http: HttpClient) { }

    public getReview(url: string): Observable<Review> {
        return this.http.get<ReviewDto>(url).pipe(
            map(mapReview)
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
                map((response) => {
                    const reviewsDto = response.body || [];
                    const linkHeader = response.headers.get('Link');
                    const paginationInfo = parseLinkHeader(linkHeader);

                    const reviews = reviewsDto.map(mapReview);

                    return {
                        reviews,
                        totalPages: paginationInfo.totalPages,
                        currentPage: paginationInfo.currentPage,
                    };
                }),
                catchError((error) => {
                    console.error('Error fetching reviews:', error);
                    return throwError(() => new Error('Failed to fetch reviews.'));
                })
            );
    }

    public createReview(reviewUrl: string, reviewDto: ReviewDto): Observable<string> {
        return this.http.post(reviewUrl, reviewDto, { observe: 'response' }).pipe(
            map(response => {
                const locationHeader = response.headers.get('Location');
                if (!locationHeader) {
                    throw new Error('Location header not found in response.');
                }
                return locationHeader;
            }),
            catchError(error => {
                console.error('Error creating review:', error);
                return throwError(() => new Error('Failed to create review.'));
            })
        );
    }
}

export function mapReview(reviewDto: ReviewDto): Review {
    return {
        rating: reviewDto.rating,
        message: reviewDto.message,
        createdAt: reviewDto.creationDate,
        self: reviewDto._links.self
    } as Review;
}