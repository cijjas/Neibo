import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { UserDto } from "../models/user";
import {map} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class LoggedInService {
    private loggedUserDtoSubject: BehaviorSubject<UserDto | null> = new BehaviorSubject<UserDto | null>(null);
    private authToken: string;

    // Initial state
    private loggedUser: {
        userId: number;
        neighborhoodId: number;
        mail: string;
        surname: string;
        name: string;
        darkMode: boolean;
    } = null;

    constructor() {
        // Subscribe to changes and update the loggedUser property
        this.loggedUserDtoSubject.subscribe(userDto => {
            if (userDto) {
                this.loggedUser = {
                    userId: this.getUserIdFromUserDto(userDto),
                    neighborhoodId: this.getNeighborhoodIdFromUserDto(userDto),
                    mail: userDto.mail,
                    name: userDto.name,
                    surname: userDto.surname,
                    darkMode: userDto.darkMode,
                };
                console.log('Logged user:', this.loggedUser);
            }
        });
    }

    private getNeighborhoodIdFromUserDto(userDto: UserDto): number {
        const regex = /\/neighborhoods\/(\d+)$/;
        const match = userDto.neighborhood.match(regex);
        return match ? parseInt(match[1], 10) : null;
    }

    private getUserIdFromUserDto(userDto: UserDto): number {
        const regex = /\/users\/(\d+)$/;
        const match = userDto.self.match(regex);
        return match ? parseInt(match[1], 10) : null;
    }

    public setLoggedUserInformation(userDto: UserDto) {
        this.loggedUserDtoSubject.next(userDto);
    }

    public getLoggedUser(): Observable<{
        userId: number;
        neighborhoodId: number;
        mail: string;
        surname: string;
        name: string;
        darkMode: boolean;
    }> {
        return this.loggedUserDtoSubject.asObservable().pipe(
            map(userDto => {
                if (userDto) {
                    return {
                        userId: this.getUserIdFromUserDto(userDto),
                        neighborhoodId: this.getNeighborhoodIdFromUserDto(userDto),
                        mail: userDto.mail,
                        name: userDto.name,
                        surname: userDto.surname,
                        darkMode: userDto.darkMode,
                    };
                }
                return null;
            })
        );
    }

    public getLoggedUserId(): Observable<number> {
        return this.getLoggedUser().pipe(map(user => user ? user.userId : null));
    }

    public getLoggedUserNeighborhoodId(): Observable<number> {
        return this.getLoggedUser().pipe(map(user => user ? user.neighborhoodId : null));
    }

    public getLoggedUserDto(): Observable<UserDto | null> {
        return this.loggedUserDtoSubject.asObservable();
    }

    public setAuthToken(authToken: string) {
        this.authToken = authToken;
    }

    public getAuthToken(): string {
        return this.authToken;
    }

    public clear() {
        this.loggedUserDtoSubject.next(null);
        this.authToken = null;
        this.loggedUser = null;
    }
}
