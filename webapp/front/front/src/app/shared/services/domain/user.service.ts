import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { Injectable } from '@angular/core';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import {
  LanguageDto,
  UserDto,
  UserRoleDto,
  User,
  parseLinkHeader,
  Roles,
  LinkKey,
} from '@shared/index';
import { HateoasLinksService, ImageService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(
    private http: HttpClient,
    private linkService: HateoasLinksService,
    private imageService: ImageService
  ) {}

  public getUser(userUrl: string): Observable<User> {
    return this.http
      .get<UserDto>(userUrl)
      .pipe(mergeMap((userDto) => mapUser(this.http, userDto)));
  }

  public getUsers(
    queryParams: {
      userRole?: string;
      page?: number;
      size?: number;
    } = {}
  ): Observable<{ users: User[]; totalPages: number; currentPage: number }> {
    let userUrl: string = this.linkService.getLink(LinkKey.NEIGHBORHOOD_USERS);

    let params = new HttpParams();

    if (queryParams.page !== undefined)
      params = params.set('page', queryParams.page.toString());
    if (queryParams.size !== undefined)
      params = params.set('size', queryParams.size.toString());
    if (queryParams.userRole)
      params = params.set('withRole', queryParams.userRole);

    return this.http
      .get<UserDto[]>(userUrl, { params, observe: 'response' })
      .pipe(
        mergeMap((response) => {
          const usersDto = response.body || [];
          const linkHeader = response.headers.get('Link');
          const paginationInfo = parseLinkHeader(linkHeader);

          const userObservables = usersDto.map((userDto) =>
            mapUser(this.http, userDto)
          );

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

  // ? Prequest is kinda sus, waiting for users refactoring
  public createUser(
    neighborhoodUrl: string,
    name: string,
    surname: string,
    password: string,
    mail: string,
    language: string,
    identification: number
  ): Observable<string | null> {
    let usersUrl: string = this.linkService.getLink(LinkKey.USERS);
    let unverifiedUserRole: string = this.linkService.getLink(
      LinkKey.UNVERIFIED_NEIGHBOR_USER_ROLE
    );

    const body: UserDto = {
      name: name,
      surname: surname,
      password: password,
      mail: mail,
      userRole: unverifiedUserRole,
      language: language,
      identification: identification,
      neighborhood: neighborhoodUrl,
    };

    return this.http.post(usersUrl, body, { observe: 'response' }).pipe(
      map((response) => {
        const locationHeader = response.headers.get('Location');
        if (locationHeader) {
          return locationHeader;
        } else {
          console.error('Location header not found');
          return null;
        }
      }),
      catchError((error) => {
        console.error('Error creating User', error);
        return of(null);
      })
    );
  }

  public createWorkerUser(
    name: string,
    surname: string,
    password: string,
    mail: string,
    language: string,
    identification: number
  ): Observable<string | null> {
    let usersUrl: string = this.linkService.getLink(LinkKey.USERS);
    let workerUserRole: string = this.linkService.getLink(
      LinkKey.WORKER_USER_ROLE
    );
    let workersNeighborhoodUrl: string = this.linkService.getLink(
      LinkKey.WORKERS_NEIGHBORHOOD
    );

    const body: UserDto = {
      name: name,
      surname: surname,
      password: password,
      mail: mail,
      userRole: workerUserRole,
      language: language,
      identification: identification,
      neighborhood: workersNeighborhoodUrl,
    };

    return this.http.post(usersUrl, body, { observe: 'response' }).pipe(
      map((response) => {
        const locationHeader = response.headers.get('Location');
        if (locationHeader) {
          return locationHeader;
        } else {
          console.error('Location header not found');
          return null;
        }
      }),
      catchError((error) => {
        console.error('Error creating User', error);
        return of(null);
      })
    );
  }

  public toggleDarkMode(user: User): Observable<User> {
    const updatedDarkMode = !user.darkMode;
    const updateUrl = user.self;

    return this.http
      .patch<UserDto>(updateUrl, { darkMode: updatedDarkMode })
      .pipe(
        mergeMap((updatedUserDto) => mapUser(this.http, updatedUserDto)),
        catchError((error) => {
          console.error('Failed to toggle dark mode:', error);
          return of(user);
        })
      );
  }

  public toggleLanguage(user: User): Observable<User> {
    const languageLinks = {
      SPANISH: this.linkService.getLink(LinkKey.SPANISH_LANGUAGE),
      ENGLISH: this.linkService.getLink(LinkKey.ENGLISH_LANGUAGE),
    };

    const currentLanguage = user.language;
    const newLanguage =
      currentLanguage === languageLinks.SPANISH
        ? languageLinks.ENGLISH
        : languageLinks.SPANISH;

    const updateUrl = user.self;

    return this.http.patch<UserDto>(updateUrl, { language: newLanguage }).pipe(
      mergeMap((updatedUserDto) => mapUser(this.http, updatedUserDto)),
      catchError((error) => {
        console.error('Failed to toggle language:', error);
        return of(user);
      })
    );
  }

  public updatePhoneNumber(
    userUrl: string,
    phoneNumber: string
  ): Observable<User> {
    return this.http
      .patch<UserDto>(userUrl, { phoneNumber: phoneNumber })
      .pipe(mergeMap((updatedUserDto) => mapUser(this.http, updatedUserDto)));
  }

  public requestNeighborhood(newNeighborhoodUrl: string): Observable<User> {
    let userUrl: string = this.linkService.getLink(LinkKey.USER_SELF);
    let unverifiedUserRole: string = this.linkService.getLink(
      LinkKey.UNVERIFIED_NEIGHBOR_USER_ROLE
    );

    let body: UserDto = {
      neighborhood: newNeighborhoodUrl,
      userRole: unverifiedUserRole,
    };

    return this.http
      .patch<UserDto>(userUrl, body)
      .pipe(mergeMap((updatedUserDto) => mapUser(this.http, updatedUserDto)));
  }

  public uploadProfilePicture(user: User, file: File): Observable<User> {
    return this.imageService.createImage(file).pipe(
      mergeMap((imageUrl: string) => {
        const updateUrl = user.self;
        return this.http
          .patch<UserDto>(updateUrl, { profilePicture: imageUrl })
          .pipe(
            mergeMap((updatedUserDto) => mapUser(this.http, updatedUserDto))
          );
      })
    );
  }

  public verifyUser(user: User): Observable<User> {
    let neighborUserRoleUrl: string = this.linkService.getLink(
      LinkKey.NEIGHBOR_USER_ROLE
    );
    return this.http
      .patch<UserDto>(user.self, { userRole: neighborUserRoleUrl })
      .pipe(mergeMap((newUser) => mapUser(this.http, newUser)));
  }

  public rejectUser(user: User): Observable<User> {
    let rejectedUserRoleUrl: string = this.linkService.getLink(
      LinkKey.REJECTED_USER_ROLE
    );
    return this.http
      .patch<UserDto>(user.self, { userRole: rejectedUserRoleUrl })
      .pipe(mergeMap((newUser) => mapUser(this.http, newUser)));
  }
}

export function mapUser(http: HttpClient, userDto: UserDto): Observable<User> {
  const roleDisplayMapping = {
    [Roles.ADMINISTRATOR]: 'Administrator',
    [Roles.NEIGHBOR]: 'Neighbor',
    [Roles.UNVERIFIED_NEIGHBOR]: 'Unverified Neighbor',
    [Roles.WORKER]: 'Service Provider',
    [Roles.REJECTED]: 'Rejected',
  };

  return forkJoin([
    http.get<LanguageDto>(userDto._links.language),
    http.get<UserRoleDto>(userDto._links.userRole),
  ]).pipe(
    map(([language, userRole]) => {
      const roleEnum = userRole.role as Roles;
      return {
        email: userDto.mail,
        name: userDto.name,
        surname: userDto.surname,
        darkMode: userDto.darkMode,
        phoneNumber: userDto.phoneNumber,
        image: userDto._links.userImage,
        identification: userDto.identification,
        creationDate: userDto.creationDate,
        language: language._links.self,
        userRole: userRole.role,
        userRoleDisplay: roleDisplayMapping[roleEnum] || 'Unknown Role', // Map to display-friendly name
        userRoleEnum: roleEnum, // Add enum
        self: userDto._links.self,
      } as User;
    })
  );
}
