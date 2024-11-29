import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { map, mergeMap } from 'rxjs/operators'
import { User } from '../../model/index'
import { ImageDto, LanguageDto, UserDto, UserRoleDto } from '../../dtos/app-dtos'
import { UserForm } from '../../models/user'
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

    public getUsers(userUrl: string, userRole: string, page: number, size: number): Observable<User[]> {
        let params = new HttpParams();

        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<UserDto[]>(userUrl, { params }).pipe(
            mergeMap((usersDto: UserDto[]) => {
                const userObservables = usersDto.map(userDto => mapUser(this.http, userDto));
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


export function mapUser(http: HttpClient, userDto: UserDto): Observable<User> {
    return forkJoin([
        http.get<ImageDto>(userDto._links.profilePicture),
        http.get<LanguageDto>(userDto._links.language),
        http.get<UserRoleDto>(userDto._links.posts)
    ]).pipe(
        map(([profilePicture, language, userRole]) => {
            return {
                mail: userDto.mail,
                name: userDto.name,
                surname: userDto.surname,
                darkMode: userDto.darkMode,
                phoneNumber: userDto.phoneNumber,
                image: profilePicture.image,
                identification: userDto.identification,
                creationDate: userDto.creationDate,
                language: language.language,
                userRole: userRole.userRole,
                self: userDto._links.self
            } as User;
        })
    );
}
