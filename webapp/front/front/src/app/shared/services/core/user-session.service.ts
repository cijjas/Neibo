import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { Neighborhood, User } from "../../models/index";

// this should manage the static information from both neighborhood and user, which basically are the user na
@Injectable({
    providedIn: 'root'
})
export class UserSessionService {
    private loggedUserSubject = new BehaviorSubject<User | null>(null);
    private authToken: string | null = null;

    constructor() {
        const storedUser = localStorage.getItem('loggedUser');
        if (storedUser) {
            this.loggedUserSubject.next(JSON.parse(storedUser));
        }

        this.authToken = localStorage.getItem('authToken');
    }

    setUserInformation(user: User): void {
        this.loggedUserSubject.next(user);
        localStorage.setItem('loggedUser', JSON.stringify(user));
    }

    setNeighborhoodInformation(neighborhood: Neighborhood) {
        return null
    }

    getLoggedUser(): Observable<User | null> {
        return this.loggedUserSubject.asObservable();
    }

    setAccessToken(token: string): void {
        this.authToken = token;
        localStorage.setItem('authToken', token);
    }

    getAccessToken(): string | null {
        return this.authToken;
    }

    clear(): void {
        this.loggedUserSubject.next(null);
        this.authToken = null;
        localStorage.removeItem('loggedUser');
        localStorage.removeItem('authToken');
    }
}
