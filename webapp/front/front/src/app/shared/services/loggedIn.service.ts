import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { UserDto } from "../models/user";
import { map } from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class LoggedInService {
    private loggedUserDtoSubject: BehaviorSubject<UserDto | null>;
    private authToken: string;
    private bearerAuthToken: string;

    // Initial state
    private loggedUser: {
        user: string;
        neighborhood: string;
        mail: string;
        surname: string;
        name: string;
        darkMode: boolean;
    } = null;

    constructor() {
        // Initialize loggedUserDtoSubject with data from localStorage
        const storedUser = localStorage.getItem('loggedUserDto');
        const userDto = storedUser ? JSON.parse(storedUser) : null;
        this.loggedUserDtoSubject = new BehaviorSubject<UserDto | null>(userDto);

        // Subscribe to changes and update the loggedUser property
        this.loggedUserDtoSubject.subscribe(userDto => {
            if (userDto) {
                this.loggedUser = {
                    user: this.getUserURNFromUserDto(userDto),
                    neighborhood: this.getNeighborhoodURNFromUserDto(userDto),
                    mail: userDto.mail,
                    name: userDto.name,
                    surname: userDto.surname,
                    darkMode: userDto.darkMode,
                };
                // Store the user data in localStorage
                localStorage.setItem('loggedUserDto', JSON.stringify(userDto));
            } else {
                localStorage.removeItem('loggedUserDto');
            }
        });

        // Initialize authToken from localStorage
        this.authToken = localStorage.getItem('authToken');
    }

    private getNeighborhoodURNFromUserDto(userDto: UserDto): string {
        return userDto._links.neighborhood
    }

    private getUserURNFromUserDto(userDto: UserDto): string {
        return userDto._links.self
    }

    public setLoggedUserInformation(userDto: UserDto) {
        this.loggedUserDtoSubject.next(userDto);
    }

    public getLoggedUser(): Observable<UserDto> {
        return this.loggedUserDtoSubject.asObservable()
    }

    public getLoggedUserURN(): Observable<string> {
        return this.loggedUserDtoSubject.asObservable().pipe(
            map(userDto => userDto ? this.getUserURNFromUserDto(userDto) : null)
        );
    }

    public getLoggedUserNeighborhoodURN(): Observable<string> {
        return this.loggedUserDtoSubject.asObservable().pipe(
            map(userDto => userDto ? this.getNeighborhoodURNFromUserDto(userDto) : null)
        );
    }

    public getLoggedUserDto(): Observable<UserDto | null> {
        return this.loggedUserDtoSubject.asObservable();
    }

    public setAuthToken(authToken: string) {
        this.authToken = authToken;
        localStorage.setItem('authToken', authToken);
    }

    public getAuthToken(): string {
        return this.authToken;
    }

    public setBearerAuthToken(bearerAuthToken: string) {
        this.bearerAuthToken = bearerAuthToken;
        localStorage.setItem('bearerAuthToken', bearerAuthToken);
    }

    public getBearerAuthToken(): string {
        return this.bearerAuthToken;
    }

    public clear() {
        this.loggedUserDtoSubject.next(null);
        this.authToken = null;
        this.loggedUser = null;
        localStorage.removeItem('loggedUserDto');
        localStorage.removeItem('authToken');
    }
}
