import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Neighborhood, User } from '@shared/index';
import { HateoasLinksService } from './link.service';

@Injectable({
  providedIn: 'root',
})
export class UserSessionService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private neighborhoodSubject = new BehaviorSubject<Neighborhood | null>(null);

  // We still keep a *memory* copy of the token if we want quick access,
  // but the full reading/writing is done in TokenService now.
  private authToken: string | null = null;

  constructor(private linkService: HateoasLinksService) {
    // Restore user from localStorage if we want to persist them
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }

    // Restore neighborhood from localStorage if we want to persist it
    const storedNeighborhood = localStorage.getItem('neighborhood');
    if (storedNeighborhood) {
      this.neighborhoodSubject.next(JSON.parse(storedNeighborhood));
    }
  }

  // ----------------------------------------------
  //  User info
  // ----------------------------------------------
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

  // ----------------------------------------------
  //  Neighborhood info
  // ----------------------------------------------
  setNeighborhoodInformation(neighborhood: Neighborhood): void {
    this.neighborhoodSubject.next(neighborhood);
    localStorage.setItem('neighborhood', JSON.stringify(neighborhood));
  }

  // ----------------------------------------------
  //  Observables to subscribe to
  // ----------------------------------------------
  getCurrentUser(): Observable<User | null> {
    return this.currentUserSubject.asObservable();
  }

  getNeighborhood(): Observable<Neighborhood | null> {
    return this.neighborhoodSubject.asObservable();
  }

  // ----------------------------------------------
  //  Token (optional to keep in memory)
  // ----------------------------------------------
  setAccessToken(token: string): void {
    this.authToken = token;
  }

  getAccessToken(): string | null {
    return this.authToken;
  }
}
