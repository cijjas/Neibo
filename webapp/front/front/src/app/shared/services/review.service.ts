import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Review, ReviewDto, ReviewForm } from '../models/review'
import { WorkerDto } from "../models/worker"
import { UserDto } from "../models/user"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class ReviewService {
    private apiServerUrl = environment.apiBaseUrl
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService,
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }

    public getReview(review: string): Observable<Review> {
        const reviewDto$ = this.http.get<ReviewDto>(review, { headers: this.headers });

        return reviewDto$.pipe(
            mergeMap((reviewDto: ReviewDto) => {
                return forkJoin([
                    this.http.get<WorkerDto>(reviewDto._links.worker),
                    this.http.get<UserDto>(reviewDto._links.user)
                ]).pipe(
                    map(([worker, user]) => {
                        return {
                            rating: reviewDto.rating,
                            review: reviewDto.review,
                            date: reviewDto.date,
                            worker: worker,
                            user: user,
                            self: reviewDto._links.self
                        } as Review;
                    })
                );
            })
        );
    }

    public getReviews(worker: string, page: number, size: number): Observable<Review[]> {
        let params = new HttpParams()
        
        if(page) params = params.set('page', page.toString())
        if(size) params = params.set('size', size.toString())

        return this.http.get<ReviewDto[]>(`${worker}/reviews`, { params, headers: this.headers }).pipe(
            mergeMap((reviewsDto: ReviewDto[]) => {
                const reviewObservables = reviewsDto.map(reviewDto =>
                    forkJoin([
                        this.http.get<WorkerDto>(reviewDto._links.worker),
                        this.http.get<UserDto>(reviewDto._links.user)
                    ]).pipe(
                        map(([worker, user]) => {
                            return {
                                rating: reviewDto.rating,
                                review: reviewDto.review,
                                date: reviewDto.date,
                                worker: worker,
                                user: user,
                                self: reviewDto._links.self
                            } as Review;
                        })
                    )
                );

                 return forkJoin(reviewObservables);
            })
        );
    }

    public addReview(worker: string, review: ReviewForm): Observable<ReviewForm> {
        return this.http.post<ReviewForm>(`${worker}/reviews`, review, { headers: this.headers })
    }

}
