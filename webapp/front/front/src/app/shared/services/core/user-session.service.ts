import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { User } from "../../models/index";

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

    setLoggedUserInformation(user: User): void {
        this.loggedUserSubject.next(user);
        localStorage.setItem('loggedUser', JSON.stringify(user));
    }

    getLoggedUser(): Observable<User | null> {
        return this.loggedUserSubject.asObservable();
    }

    setAuthToken(token: string): void {
        this.authToken = token;
        localStorage.setItem('authToken', token);
    }
    

    getAuthToken(): string | null {
        return this.authToken;
    }

    clear(): void {
        this.loggedUserSubject.next(null);
        this.authToken = null;
        localStorage.removeItem('loggedUser');
        localStorage.removeItem('authToken');
    }
}
