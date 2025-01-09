import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {
  BehaviorSubject,
  catchError,
  forkJoin,
  mergeMap,
  Observable,
  of,
  tap,
  throwError,
} from 'rxjs';
import {
  NeighborhoodDto,
  UserDto,
  mapNeighborhood,
  mapUser,
  LinkKey,
  Roles,
} from '@shared/index';
import { UserSessionService } from './user-session.service';
import { HateoasLinksService } from './link.service';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly apiServerUrl = environment.apiBaseUrl;

  // Moved from UserSessionService:
  private currentRole: Roles | null = null;

  private roleMapping = {
    [LinkKey.ADMINISTRATOR_USER_ROLE]: Roles.ADMINISTRATOR,
    [LinkKey.NEIGHBOR_USER_ROLE]: Roles.NEIGHBOR,
    [LinkKey.UNVERIFIED_NEIGHBOR_USER_ROLE]: Roles.UNVERIFIED_NEIGHBOR,
    [LinkKey.REJECTED_USER_ROLE]: Roles.REJECTED,
    [LinkKey.WORKER_USER_ROLE]: Roles.WORKER,
    [LinkKey.UNVERIFIED_WORKER_ROLE]: Roles.UNVERIFIED_WORKER,
  };

  constructor(
    private http: HttpClient,
    private linkRegistry: HateoasLinksService,
    private userSessionService: UserSessionService,
    private tokenService: TokenService
  ) {}

  // ----------------------------------------------
  //  LOGIN
  // ----------------------------------------------
  login(
    mail: string,
    password: string,
    rememberMe: boolean
  ): Observable<boolean> {
    // 1. Set the "rememberMe" choice
    this.tokenService.setRememberMe(rememberMe);
    // 2. Clear existing tokens
    this.tokenService.clearTokens();

    const headers = new HttpHeaders({
      Authorization: 'Basic ' + btoa(`${mail}:${password}`),
    });

    return this.http
      .get<any>(`${this.apiServerUrl}/`, { headers, observe: 'response' })
      .pipe(
        mergeMap((response) => {
          // Extract tokens
          const accessToken = response.headers.get('X-Access-Token');
          const refreshToken = response.headers.get('X-Refresh-Token');
          const userUrl = this.extractUrl(response.headers.get('X-User-URL'));
          const neighUrl = this.extractUrl(
            response.headers.get('X-Neighborhood-URL')
          );
          const workersNeighUrl = this.extractUrl(
            response.headers.get('X-Workers-Neighborhood-URL')
          );

          // Store tokens using TokenService
          if (accessToken) {
            this.tokenService.setAccessToken(accessToken);
          }
          if (refreshToken) {
            this.tokenService.setRefreshToken(refreshToken);
          }

          // 1) Load user data (if present)
          const userObs = userUrl
            ? this.http.get<UserDto>(userUrl).pipe(
                tap((userDto) => {
                  // Register links from userDto
                  if (userDto._links) {
                    this.linkRegistry.registerLinks(userDto._links, 'user:');
                  }
                  // Derive role from userDto link
                  const userRoleLink = userDto._links?.userRole;
                  if (userRoleLink) {
                    const role = this.mapLinkToRole(userRoleLink);
                    if (role) {
                      this.setUserRole(role);
                    }
                  }
                  // Map user data
                  mapUser(this.http, userDto).subscribe({
                    next: (user) =>
                      this.userSessionService.setUserInformation(user),
                  });
                })
              )
            : of(null);

          // 2) Load neighborhood data
          const neighObs = neighUrl
            ? this.http.get<NeighborhoodDto>(neighUrl).pipe(
                tap((neighDto) => {
                  if (neighDto._links) {
                    this.linkRegistry.registerLinks(
                      neighDto._links,
                      'neighborhood:'
                    );
                  }
                  this.userSessionService.setNeighborhoodInformation(
                    mapNeighborhood(neighDto)
                  );
                })
              )
            : of(null);

          // 3) Load workers-neighborhood data (if present)
          const workersNeighObs = workersNeighUrl
            ? this.http.get<NeighborhoodDto>(workersNeighUrl).pipe(
                tap((workersNeighDto) => {
                  if (workersNeighDto._links) {
                    this.linkRegistry.registerLinks(
                      workersNeighDto._links,
                      'workerNeighborhood:'
                    );
                  }
                }),
                catchError((error) => {
                  console.error('Error fetching workers neighborhood:', error);
                  return of(null);
                })
              )
            : of(null);

          // Wait for user + neighborhood + workers neighborhood
          return forkJoin([userObs, neighObs, workersNeighObs]).pipe(
            mergeMap(() => of(true))
          );
        }),
        catchError((error) => {
          console.error('Authentication error:', error);
          return of(false);
        })
      );
  }

  // ----------------------------------------------
  // REFRESH TOKEN
  // ----------------------------------------------
  refreshToken(): Observable<boolean> {
    // TODO que funcione
    console.log('refreshing token');
    const refreshToken = this.tokenService.getRefreshToken();
    const authToken = this.tokenService.getAccessToken();
    console.log(`refresh: ref ${refreshToken}`);
    console.log(`refresh: acc ${authToken}`);

    if (!refreshToken) {
      console.log('no refresh token');
      return of(false);
    }

    const url = `${this.apiServerUrl}/`;
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.tokenService.getAccessToken()}`,
      'X-Refresh-Token': this.tokenService.getRefreshToken(),
    });

    return this.http.get<any>(url, { headers, observe: 'response' }).pipe(
      tap((response) => {
        console.log('Refresh response:', response);
        const newAccessToken =
          response.body?.accessToken || response.headers.get('X-Access-Token');
        console.log('new' + newAccessToken);
        if (!newAccessToken) {
          throw new Error('No tokens returned in refresh response');
        }
        this.tokenService.setAccessToken(newAccessToken);
      }),
      catchError((error) => {
        console.error('Refresh token failed:', error);
        if (error.status === 401) {
          console.warn('Refresh token expired or invalid. Logging out...');
        }
        this.logout();
        return throwError(() => error);
      }),
      mergeMap(() => of(true))
    );
  }

  /**
   * Convenient helper that checks if we need a refresh.
   * (You might want to add logic to check token expiry.)
   */
  refreshTokenIfNeeded(): Observable<boolean> {
    if (!this.isLoggedIn()) {
      return of(false);
    }

    if (!this.tokenService.isAccessTokenExpiringSoon()) {
      return of(true); // The token is still good enough
    }

    return this.refreshToken().pipe(catchError(() => of(false)));
  }

  // ----------------------------------------------
  // LOGOUT
  // ----------------------------------------------
  logout(): void {
    // Clear tokens
    this.tokenService.clearTokens();
    // Clear entire storage
    sessionStorage.clear();
    localStorage.clear();

    // Notify other services
    this.userSessionService.logout();
    this.linkRegistry.clearLinks();
    // (Optionally navigate to login route, etc.)
  }

  // ----------------------------------------------
  // HELPER: Are we currently logged in?
  // ----------------------------------------------
  isLoggedIn(): boolean {
    const token = this.tokenService.getAccessToken();
    return !!token;
  }

  // ----------------------------------------------
  // ROLE HANDLING (moved from userSessionService)
  // ----------------------------------------------
  public setUserRole(role: Roles): void {
    this.currentRole = role;
    localStorage.setItem('currentUserRole', role);
  }

  public getCurrentRole(): Roles | null {
    if (!this.currentRole) {
      const saved = localStorage.getItem('currentUserRole') as Roles;
      if (saved) this.currentRole = saved;
    }
    return this.currentRole;
  }

  /**
   * Map a userRole link from the server to our internal Roles enum
   */
  public mapLinkToRole(link: string | null | undefined): Roles | null {
    if (!link) return null;
    for (const [linkKey, role] of Object.entries(this.roleMapping)) {
      const knownUrl = this.linkRegistry.getLink(linkKey as LinkKey);
      if (knownUrl === link) {
        return role;
      }
    }
    return null;
  }

  // ----------------------------------------------
  // PRIVATE
  // ----------------------------------------------
  private extractUrl(headerValue: string | null): string | null {
    return headerValue?.split(';')[0].replace(/[<>]/g, '') ?? null;
  }
}
