import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Neighborhood, User, LinkKey, Roles } from '@shared/index';
import { HateoasLinksService } from './link.service';

@Injectable({
  providedIn: 'root',
})
export class UserSessionService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private neighborhoodSubject = new BehaviorSubject<Neighborhood | null>(null);
  private authToken: string | null = null;
  private currentRole: Roles | null = null;

  private roleMapping = {
    [LinkKey.ADMINISTRATOR_USER_ROLE]: Roles.ADMINISTRATOR,
    [LinkKey.NEIGHBOR_USER_ROLE]: Roles.NEIGHBOR,
    [LinkKey.UNVERIFIED_NEIGHBOR_USER_ROLE]: Roles.UNVERIFIED_NEIGHBOR,
    [LinkKey.REJECTED_USER_ROLE]: Roles.REJECTED,
    [LinkKey.WORKER_USER_ROLE]: Roles.WORKER,
    [LinkKey.UNVERIFIED_WORKER_ROLE]: Roles.UNVERIFIED_WORKER,
    // additional roles
  };

  constructor(private linkService: HateoasLinksService) {
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

  setUserRole(role: Roles): void {
    this.currentRole = role;
    localStorage.setItem('currentUserRole', role);
  }

  getCurrentRole(): Roles | null {
    // If we don't have it in memory, check localStorage
    if (!this.currentRole) {
      const saved = localStorage.getItem('currentUserRole') as Roles;
      if (saved) this.currentRole = saved;
    }
    return this.currentRole;
  }

  mapLinkToRole(link: string | null | undefined): Roles | null {
    if (!link) return null;

    // We loop over the roleMapping to see which root: link matches
    for (const [linkKey, role] of Object.entries(this.roleMapping)) {
      const urlForLinkKey = this.linkService.getLink(linkKey as LinkKey);
      if (urlForLinkKey === link) {
        return role;
      }
    }

    return null;
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
    localStorage.removeItem('currentUser');
    localStorage.removeItem('neighborhood');
    localStorage.removeItem('authToken');
  }
}
