import { HttpClient, HttpParams } from '@angular/common/http'
import { User, UserDto, UserForm } from '../models/user'
import { NeighborhoodDto } from "../models/neighborhood"
import { ImageDto } from "../models/image"
import { CommentDto } from "../models/comment"
import { PostDto } from "../models/post"
import { BookingDto } from "../models/booking"
import { ProductDto } from "../models/product"
import { EventDto } from "../models/event"
import {Observable, forkJoin, tap} from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class UserService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getUser(neighborhoodId: number, userId: number): Observable<User> {
        const userDto$ = this.http.get<UserDto>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/users/${userId}`);

        return userDto$.pipe(
          tap(userDto => console.log('UserDto:', userDto)), // Log the received UserDto

          mergeMap((userDto: UserDto) => {
                console.log('1');
                return forkJoin([
                    this.http.get<NeighborhoodDto>(userDto.neighborhood),
                    this.http.get<ImageDto>(userDto.profilePicture),
                    this.http.get<CommentDto[]>(userDto.comments),
                    this.http.get<PostDto[]>(userDto.posts),
                    this.http.get<BookingDto[]>(userDto.bookings),
                    this.http.get<PostDto[]>(userDto.subscribedPosts),
                    this.http.get<PostDto[]>(userDto.likedPosts),
                    this.http.get<ProductDto[]>(userDto.purchases),
                    this.http.get<ProductDto[]>(userDto.sales),
                    this.http.get<EventDto[]>(userDto.eventsSubscribed)

                ]).pipe(

                    map(([
                        neighborhood,
                       profilePicture,
                       comments,
                       posts,
                       bookings,
                       subscribedPosts,
                       likedPosts,
                      purchases,
                       sales,
                       eventsSubscribed
                         ]) => {
                      console.log('2');

                        return {
                            userId: userDto.userId,
                            mail: userDto.mail,
                            name: userDto.name,
                            surname: userDto.surname,
                            password: userDto.password,
                            neighborhood: neighborhood,
                            darkMode: userDto.darkMode,
                            phoneNumber: userDto.phoneNumber,
                            profilePicture: profilePicture,
                            identification: userDto.identification,
                            comments: comments,
                            posts: posts,
                            bookings: bookings,
                            subscribedPosts: subscribedPosts,
                            likedPosts: likedPosts,
                            purchases: purchases,
                            sales: sales,
                            eventsSubscribed: eventsSubscribed,
                            self: userDto.self
                        } as User;
                    })
                );
            })
        );

    }

    public getUsers(neighborhoodId: number, userRole: string, page: number, size: number): Observable<User[]> {
        const params = new HttpParams().set('userRole', userRole).set('page', page.toString()).set('size', size.toString())

        return this.http.get<UserDto[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/users`, { params }).pipe(
            mergeMap((usersDto: UserDto[]) => {
                const userObservables = usersDto.map(userDto =>
                    forkJoin([
                        this.http.get<NeighborhoodDto>(userDto.neighborhood),
                        this.http.get<ImageDto>(userDto.profilePicture),
                        this.http.get<CommentDto[]>(userDto.comments),
                        this.http.get<PostDto[]>(userDto.posts),
                        this.http.get<BookingDto[]>(userDto.bookings),
                        this.http.get<PostDto[]>(userDto.subscribedPosts),
                        this.http.get<PostDto[]>(userDto.likedPosts),
                        this.http.get<ProductDto[]>(userDto.purchases),
                        this.http.get<ProductDto[]>(userDto.sales),
                        this.http.get<EventDto[]>(userDto.eventsSubscribed)
                    ]).pipe(
                        map(([neighborhood, profilePicture, comments, posts, bookings, subscribedPosts, likedPosts,
                            purchases, sales, eventsSubscribed]) => {
                        return {
                            userId: userDto.userId,
                            mail: userDto.mail,
                            name: userDto.name,
                            surname: userDto.surname,
                            password: userDto.password,
                            neighborhood: neighborhood,
                            darkMode: userDto.darkMode,
                            phoneNumber: userDto.phoneNumber,
                            profilePicture: profilePicture,
                            identification: userDto.identification,
                            comments: comments,
                            posts: posts,
                            bookings: bookings,
                            subscribedPosts: subscribedPosts,
                            likedPosts: likedPosts,
                            purchases: purchases,
                            sales: sales,
                            eventsSubscribed: eventsSubscribed,
                            self: userDto.self
                        } as User;
                    })
                    )
                );

                 return forkJoin(userObservables);
            })
        );
    }

    public addUser(neighborhoodId: number, user: UserForm): Observable<UserForm> {
        return this.http.post<UserForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/users`, user)
    }

    public updateUser(neighborhoodId: number, user: UserForm): Observable<UserForm> {
        return this.http.patch<UserForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/users/${user.userId}`, user)
    }

}
