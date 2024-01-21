import { HttpClient, HttpParams } from '@angular/common/http'
import { Review, ReviewDto, ReviewForm } from './review'
import { WorkerDto } from "./worker"
import { UserDto } from "./user"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class ReviewService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getReview(workerId: number, reviewId: number): Observable<Review> {
        const reviewDto$ = this.http.get<ReviewDto>(`${this.apiServerUrl}/workers/${workerId}/reviews/${reviewId}`);
            
        return reviewDto$.pipe(
            mergeMap((reviewDto: ReviewDto) => {
                return forkJoin([
                    this.http.get<WorkerDto>(reviewDto.worker),
                    this.http.get<UserDto>(reviewDto.user)
                ]).pipe(
                    map(([worker, user]) => {
                        return {
                            reviewId: reviewDto.reviewId,
                            rating: reviewDto.rating,
                            review: reviewDto.review,
                            date: reviewDto.date,
                            worker: worker,
                            user: user,
                            self: reviewDto.self
                        } as Review;
                    })
                );
            })
        );
    }

    public getReviews(workerId: number, page: number, size: number): Observable<Review[]> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString())

        return this.http.get<ReviewDto[]>(`${this.apiServerUrl}/workers/${workerId}/reviews`, { params }).pipe(
            mergeMap((reviewsDto: ReviewDto[]) => {
                const reviewObservables = reviewsDto.map(reviewDto => 
                    forkJoin([
                        this.http.get<WorkerDto>(reviewDto.worker),
                        this.http.get<UserDto>(reviewDto.user)
                    ]).pipe(
                        map(([worker, user]) => {
                            return {
                                reviewId: reviewDto.reviewId,
                                rating: reviewDto.rating,
                                review: reviewDto.review,
                                date: reviewDto.date,
                                worker: worker,
                                user: user,
                                self: reviewDto.self
                            } as Review;
                        })
                    )
                );
        
                 return forkJoin(reviewObservables);
            })
        );
    }

    public addReview(workerId: number, review: ReviewForm): Observable<ReviewForm> {
        return this.http.post<ReviewForm>(`${this.apiServerUrl}/workers/${workerId}/reviews`, review)
    }

}
