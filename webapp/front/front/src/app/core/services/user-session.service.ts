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

  constructor() {
    try {
      const storedUser = localStorage.getItem('currentUser');
      if (storedUser) {
        this.currentUserSubject.next(JSON.parse(storedUser));
      }
    } catch (error) {
      console.error('Error parsing storedUser:', error);
    }

    try {
      const storedNeighborhood = localStorage.getItem('neighborhood');
      if (storedNeighborhood) {
        this.neighborhoodSubject.next(JSON.parse(storedNeighborhood));
      }
    } catch (error) {
      console.error('Error parsing storedNeighborhood:', error);
    }
  }

  // ----------------------------------------------
  //  User info
  // ----------------------------------------------
  setUserInformation(user: User): void {
    this.currentUserSubject.next({ ...user }); // Ensure immutability
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
    this.neighborhoodSubject.next({ ...neighborhood }); // Ensure immutability
    localStorage.setItem('neighborhood', JSON.stringify(neighborhood));
  }

  // ----------------------------------------------
  //  Observables to subscribe to
  // ----------------------------------------------

  getCurrentUser(): Observable<User | null> {
    return this.currentUserSubject.asObservable();
  }

  getCurrentNeighborhood(): Observable<Neighborhood | null> {
    return this.neighborhoodSubject.asObservable();
  }

  // ----------------------------------------------
  //  Logout Method
  // ----------------------------------------------
  logout(): void {
    this.currentUserSubject.next(null);
    this.neighborhoodSubject.next(null);

    localStorage.removeItem('currentUser');
    localStorage.removeItem('neighborhood');
  }
}
