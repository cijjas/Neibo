import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { map, mergeMap } from 'rxjs/operators'
import { User } from '../../models/index'
import { ImageDto, LanguageDto, UserDto, UserRoleDto } from '../../dtos/app-dtos'
import { parseLinkHeader } from './utils'
import { HateoasLinksService, ImageService } from '../index.service'
// TODO UserForm del user

@Injectable({ providedIn: 'root' })
export class UserService {
    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService,
        private imageService: ImageService

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

    public toggleDarkMode(user: User): Observable<User> {
        const updatedDarkMode = !user.darkMode;
        const updateUrl = user.self;

        return this.http.patch<UserDto>(updateUrl, { darkMode: updatedDarkMode }).pipe(
            mergeMap((updatedUserDto) => mapUser(this.http, updatedUserDto))
        );
    }

    public toggleLanguage(user: User): Observable<User> {
        const newLanguage = user.language === 'SPANISH' ? this.linkService.getLink('neighborhood:languageEnglish') : this.linkService.getLink('neighborhood:languageSpanish');
        const updateUrl = user.self;

        return this.http.patch<UserDto>(updateUrl, { language: newLanguage }).pipe(
            mergeMap((updatedUserDto) => mapUser(this.http, updatedUserDto))
        );
    }

    public updatePhoneNumber(userUrl: string, phoneNumber: string): Observable<User> {
        return this.http.patch<UserDto>(userUrl, { phoneNumber: phoneNumber }).pipe(
            mergeMap((updatedUserDto) => mapUser(this.http, updatedUserDto))
        );
    }

    public uploadProfilePicture(user: User, file: File): Observable<User> {
        return this.imageService.createImage(file).pipe(
            mergeMap((imageUrl: string) => {
                const updateUrl = user.self;
                return this.http.patch<UserDto>(updateUrl, { profilePicture: imageUrl }).pipe(
                    mergeMap((updatedUserDto) => mapUser(this.http, updatedUserDto))
                );
            })
        );
    }
}

export function mapUser(http: HttpClient, userDto: UserDto): Observable<User> {
    return forkJoin([
        http.get<LanguageDto>(userDto._links.language),
        http.get<UserRoleDto>(userDto._links.userRole)
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
