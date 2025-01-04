import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { Neighborhood, User } from "@shared/index";

@Injectable({
    providedIn: 'root'
})
export class UserSessionService {
    private currentUserSubject = new BehaviorSubject<User | null>(null);
    private neighborhoodSubject = new BehaviorSubject<Neighborhood | null>(null);
    private authToken: string | null = null;

    constructor() {
        // Load user and token from localStorage
        const storedUser = localStorage.getItem('currentUser');
        if (storedUser) {
            this.currentUserSubject.next(JSON.parse(storedUser));
        }

        const storedNeighborhood = localStorage.getItem('neighborhood');
        if (storedNeighborhood) {
            this.neighborhoodSubject.next(JSON.parse(storedNeighborhood));
        }

        this.authToken = localStorage.getItem('authToken');
    }

    setUserInformation(user: User): void {
        this.currentUserSubject.next(user);
        localStorage.setItem('currentUser', JSON.stringify(user));
    }

    updateUserProperty(key: keyof User, value: any): void {
        const currentUser = this.currentUserSubject.value;
        if (currentUser) {
            const updatedUser = { ...currentUser, [key]: value };
            this.setUserInformation(updatedUser);
        }
    }

    setNeighborhoodInformation(neighborhood: Neighborhood): void {
        this.neighborhoodSubject.next(neighborhood);
        localStorage.setItem('neighborhood', JSON.stringify(neighborhood));
    }

    getCurrentUser(): Observable<User | null> {
        return this.currentUserSubject.asObservable();
    }

    getCurrentUserRole(): string | null {
        const currentUser = this.currentUserSubject.value;
        return currentUser ? currentUser.userRole : null;
    }



    getNeighborhood(): Observable<Neighborhood | null> {
        return this.neighborhoodSubject.asObservable();
    }

    setAccessToken(token: string): void {
        this.authToken = token;
        localStorage.setItem('authToken', token);
    }

    getAccessToken(): string | null {
        return this.authToken;
    }

    clear(): void {
        this.currentUserSubject.next(null);
        this.neighborhoodSubject.next(null);
        this.authToken = null;
        localStorage.removeItem('currentUser');
        localStorage.removeItem('neighborhood');
        localStorage.removeItem('authToken');
    }

    logout(): void {
        this.currentUserSubject.next(null);
        this.neighborhoodSubject.next(null);
        this.authToken = null;

        // Clear localStorage
        localStorage.removeItem('currentUser');
        localStorage.removeItem('neighborhood');
        localStorage.removeItem('authToken');
    }
}
