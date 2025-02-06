// auth.service.ts
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {
  catchError,
  forkJoin,
  map,
  mergeMap,
  Observable,
  of,
  switchMap,
  tap,
} from 'rxjs';
import {
  NeighborhoodDto,
  UserDto,
  mapNeighborhood,
  mapUser,
  LinkKey,
  Role,
} from '@shared/index';
import { UserSessionService } from './user-session.service';
import { HateoasLinksService } from './link.service';
import { TokenService } from './token.service';
import { Router } from '@angular/router';
import { PreferencesService } from './preferences.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly apiServerUrl = environment.apiBaseUrl;
  private channel: BroadcastChannel;

  private roleMapping = {
    [LinkKey.ADMINISTRATOR_USER_ROLE]: Role.ADMINISTRATOR,
    [LinkKey.NEIGHBOR_USER_ROLE]: Role.NEIGHBOR,
    [LinkKey.UNVERIFIED_NEIGHBOR_USER_ROLE]: Role.UNVERIFIED_NEIGHBOR,
    [LinkKey.REJECTED_USER_ROLE]: Role.REJECTED,
    [LinkKey.WORKER_USER_ROLE]: Role.WORKER,
    [LinkKey.SUPER_ADMINISTRATOR_USER_ROLE]: Role.SUPER_ADMIN,
  };

  constructor(
    private http: HttpClient,
    private linkRegistry: HateoasLinksService,
    private userSessionService: UserSessionService,
    private tokenService: TokenService,
    private router: Router,
    private preferencesService: PreferencesService,
  ) {
    this.channel = new BroadcastChannel('auth_channel');
  }

  // LOGIN
  login(mail: string, password: string): Observable<boolean> {
    this.tokenService.clearTokens();

    const headers = new HttpHeaders({
      Authorization: 'Basic ' + btoa(`${mail}:${password}`),
    });

    return this.http
      .get<any>(`${this.apiServerUrl}/`, { headers, observe: 'response' })
      .pipe(
        switchMap(response => {
          const accessToken = response.headers.get('X-Access-Token');
          const refreshToken = response.headers.get('X-Refresh-Token');
          const userUrl = this.extractUrl(response.headers.get('X-User-URL'));
          const neighUrl = this.extractUrl(
            response.headers.get('X-Neighborhood-URL'),
          );
          const workersNeighUrl = this.extractUrl(
            response.headers.get('X-Workers-Neighborhood-URL'),
          );

          if (accessToken) {
            this.tokenService.setAccessToken(accessToken);
          }
          if (refreshToken) {
            this.tokenService.setRefreshToken(refreshToken);
          }

          // Create observables for user, neighborhood, and workers neighborhood
          const userObs = userUrl
            ? this.http.get<UserDto>(userUrl).pipe(
                switchMap(userDto => {
                  if (userDto._links) {
                    this.linkRegistry.registerLinks(userDto._links, 'user:');
                  }
                  const userRoleLink = userDto._links?.userRole;
                  if (userRoleLink) {
                    const role = this.mapLinkToRole(userRoleLink);
                    if (role) {
                      this.userSessionService.setUserRole(role);
                    }
                  }
                  return mapUser(this.http, userDto);
                }),
                tap(user => {
                  this.userSessionService.setUserInformation(user);
                  this.preferencesService.applyDarkMode(user.darkMode);
                }),
                catchError(error => {
                  console.error('Error fetching user data:', error);
                  return of(null); // Continue even if user data fails
                }),
              )
            : of(null);

          const neighObs = neighUrl
            ? this.http.get<NeighborhoodDto>(neighUrl).pipe(
                tap(neighDto => {
                  if (neighDto._links) {
                    this.linkRegistry.registerLinks(
                      neighDto._links,
                      'neighborhood:',
                    );
                  }
                  this.userSessionService.setNeighborhoodInformation(
                    mapNeighborhood(neighDto),
                  );
                }),
                catchError(error => {
                  console.error('Error fetching neighborhood data:', error);
                  return of(null); // Continue even if neighborhood data fails
                }),
              )
            : of(null);

          const workersNeighObs = workersNeighUrl
            ? this.http.get<NeighborhoodDto>(workersNeighUrl).pipe(
                tap(workersNeighDto => {
                  if (workersNeighDto._links) {
                    this.linkRegistry.registerLinks(
                      workersNeighDto._links,
                      'workerNeighborhood:',
                    );
                  }
                }),
                catchError(error => {
                  console.error('Error fetching workers neighborhood:', error);
                  return of(null); // Continue even if workers neighborhood data fails
                }),
              )
            : of(null);

          // Use forkJoin to execute all observables in parallel
          return forkJoin([userObs, neighObs, workersNeighObs]).pipe(
            tap(() => {
              // Emit the 'login' event after successful login for other tabs to get notified
              this.channel.postMessage({ type: 'login' });
            }),
            map(() => true), // Indicate successful login
          );
        }),
        catchError(error => {
          console.error('Authentication error:', error);
          return of(false); // Indicate failed login
        }),
      );
  }

  // In AuthService
  logout(): void {
    this.tokenService.clearTokens();

    sessionStorage.clear();
    localStorage.clear();

    this.linkRegistry.clearLinks();
    this.router.navigate(['']);
  }

  isLoggedIn(): boolean {
    const token = this.tokenService.getRefreshToken();
    return !!token;
  }

  public mapLinkToRole(link: string | null | undefined): Role | null {
    if (!link) return null;
    for (const [linkKey, role] of Object.entries(this.roleMapping)) {
      const knownUrl = this.linkRegistry.getLink(linkKey as LinkKey);
      if (knownUrl === link) {
        return role;
      }
    }
    return null;
  }

  private extractUrl(headerValue: string | null): string | null {
    return headerValue?.split(';')[0].replace(/[<>]/g, '') ?? null;
  }
}
