import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { map, mergeMap } from 'rxjs/operators'
import { User } from '../../models/index'
import { ImageDto, LanguageDto, UserDto, UserRoleDto } from '../../dtos/app-dtos'
import { parseLinkHeader } from './utils'
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
    ): Observable<{ users: User[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.userRole) params = params.set('withRole', queryParams.userRole);

        return this.http
            .get<UserDto[]>(userUrl, { params, observe: 'response' })
            .pipe(
                mergeMap((response) => {
                    const usersDto = response.body || [];
                    const linkHeader = response.headers.get('Link');
                    const paginationInfo = parseLinkHeader(linkHeader);

                    const userObservables = usersDto.map((userDto) => mapUser(this.http, userDto));

                    return forkJoin(userObservables).pipe(
                        map((users) => ({
                            users,
                            totalPages: paginationInfo.totalPages,
                            currentPage: paginationInfo.currentPage,
                        }))
                    );
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
