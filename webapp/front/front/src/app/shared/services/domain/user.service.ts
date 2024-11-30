import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { map, mergeMap } from 'rxjs/operators'
import { User } from '../../models/index'
import { ImageDto, LanguageDto, UserDto, UserRoleDto } from '../../dtos/app-dtos'
// TODO UserForm del user

@Injectable({ providedIn: 'root' })
export class UserService {
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
    ) { }

    public getUser(userUrl: string): Observable<User> {
        return this.http.get<UserDto>(userUrl).pipe(
            mergeMap(userDto => mapUser(this.http, userDto))
        );
    }

    public getUsers(
        userUrl: string,
        queryParams: {
            userRole?: string;
            page?: number;
            size?: number;
        } = {}
    ): Observable<User[]> {
        let params = new HttpParams();

        // Add query parameters dynamically
        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.userRole) params = params.set('withRole', queryParams.userRole);

        return this.http.get<UserDto[]>(userUrl, { params }).pipe(
            mergeMap((usersDto: UserDto[]) => {
                const userObservables = usersDto.map(userDto => mapUser(this.http, userDto));
                return forkJoin(userObservables);
            })
        );
    }
}

export function mapUser(http: HttpClient, userDto: UserDto): Observable<User> {
    return forkJoin([
        http.get<LanguageDto>(userDto._links.language),
        http.get<UserRoleDto>(userDto._links.posts)
    ]).pipe(
        map(([language, userRole]) => {
            return {
                email: userDto.mail,
                name: userDto.name,
                surname: userDto.surname,
                darkMode: userDto.darkMode,
                phoneNumber: userDto.phoneNumber,
                image: userDto._links.userImage,
                identification: userDto.identification,
                creationDate: userDto.creationDate,
                language: language.name,
                userRole: userRole.role,
                self: userDto._links.self
            } as User;
        })
    );
}
