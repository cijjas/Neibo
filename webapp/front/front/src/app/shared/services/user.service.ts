import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { User, UserDto, UserForm } from '../models/user'
import { NeighborhoodDto } from "../models/neighborhood"
import { ImageDto } from "../models/image"
import { CommentDto } from "../models/comment"
import { PostDto } from "../models/post"
import { BookingDto } from "../models/booking"
import { ProductDto } from "../models/product"
import { EventDto } from "../models/event"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'
import { UserRole, UserRoleDto } from '../models/userRole'

@Injectable({ providedIn: 'root' })
export class UserService {
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

    public getUser(user: string): Observable<User> {
        const userDto$ = this.http.get<UserDto>(user, { headers: this.headers });
        return userDto$.pipe(
            mergeMap((userDto: UserDto) => {
                return forkJoin([
                    this.http.get<NeighborhoodDto>(userDto._links.neighborhood),
                    this.http.get<ImageDto>(userDto._links.profilePicture),
                    this.http.get<CommentDto[]>(userDto._links.comments),
                    this.http.get<PostDto[]>(userDto._links.posts),
                    this.http.get<BookingDto[]>(userDto._links.bookings),
                    //this.http.get<PostDto[]>(userDto._links.subscribedPosts),
                    this.http.get<PostDto[]>(userDto._links.likedPosts),
                    this.http.get<ProductDto[]>(userDto._links.purchases),
                    this.http.get<ProductDto[]>(userDto._links.sales),
                    //this.http.get<EventDto[]>(userDto._links.eventsSubscribed)
                    this.http.get<UserRoleDto>(userDto._links.userRole)
                ]).pipe(
                    map(([neighborhood, profilePicture, comments, posts, bookings, likedPosts,
                        purchases, sales, userRole]) => {
                        return {
                            mail: userDto.mail,
                            name: userDto.name,
                            surname: userDto.surname,
                            neighborhood: neighborhood,
                            darkMode: userDto.darkMode,
                            phoneNumber: userDto.phoneNumber,
                            profilePicture: profilePicture,
                            identification: userDto.identification,
                            creationDate: userDto.creationDate,
                            comments: comments,
                            posts: posts,
                            bookings: bookings,
                            //subscribedPosts: subscribedPosts,
                            likedPosts: likedPosts,
                            purchases: purchases,
                            sales: sales,
                            //eventsSubscribed: eventsSubscribed,
                            userRole: userRole,
                            self: userDto._links.self
                        } as User;
                    })
                );
            })
        );

    }

    public getUsers(neighborhood: string, userRole: string, page: number, size: number): Observable<User[]> {
        let params = new HttpParams()

        if (userRole) params = params.set('withRole', userRole)
        if (page) params = params.set('page', page.toString())
        if (size) params = params.set('size', size.toString())

        return this.http.get<UserDto[]>(`${neighborhood}/users`, { params, headers: this.headers }).pipe(
            mergeMap((usersDto: UserDto[]) => {
                const userObservables = usersDto.map(userDto =>
                    forkJoin([
                        this.http.get<NeighborhoodDto>(userDto._links.neighborhood),
                        this.http.get<ImageDto>(userDto._links.profilePicture),
                        this.http.get<CommentDto[]>(userDto._links.comments),
                        this.http.get<PostDto[]>(userDto._links.posts),
                        this.http.get<BookingDto[]>(userDto._links.bookings),
                        //this.http.get<PostDto[]>(userDto._links.subscribedPosts),
                        this.http.get<PostDto[]>(userDto._links.likedPosts),
                        this.http.get<ProductDto[]>(userDto._links.purchases),
                        this.http.get<ProductDto[]>(userDto._links.sales),
                        //this.http.get<EventDto[]>(userDto._links.eventsSubscribed)
                        this.http.get<UserRoleDto>(userDto._links.userRole)
                    ]).pipe(
                        map(([neighborhood, profilePicture, comments, posts, bookings, likedPosts,
                            purchases, sales, userRole]) => {
                            return {
                                mail: userDto.mail,
                                name: userDto.name,
                                surname: userDto.surname,
                                neighborhood: neighborhood,
                                darkMode: userDto.darkMode,
                                phoneNumber: userDto.phoneNumber,
                                profilePicture: profilePicture,
                                identification: userDto.identification,
                                creationDate: userDto.creationDate,
                                comments: comments,
                                posts: posts,
                                bookings: bookings,
                                //subscribedPosts: subscribedPosts,
                                likedPosts: likedPosts,
                                purchases: purchases,
                                sales: sales,
                                //eventsSubscribed: eventsSubscribed,
                                userRole: userRole,
                                self: userDto._links.self
                            } as User;
                        })
                    )
                );

                return forkJoin(userObservables);
            })
        );
    }

    public addUser(neighborhood: number, user: UserForm): Observable<UserForm> {
        return this.http.post<UserForm>(`${neighborhood}/users`, user, { headers: this.headers })
    }

    public updateUser(user: string, userForm: UserForm): Observable<UserForm> {
        return this.http.patch<UserForm>(user, userForm, { headers: this.headers })
    }

}
