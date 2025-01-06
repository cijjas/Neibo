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
} from '@shared/index';
import { UserSessionService } from './user-session.service';
import { HateoasLinksService } from './link.service';
import { AppInitService } from './app-init.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly apiServerUrl = environment.apiBaseUrl;
  private readonly rememberMeKey = 'rememberMe';
  private readonly authTokenKey = 'authToken';
  private readonly refreshTokenKey = 'refreshToken';

  constructor(
    private http: HttpClient,
    private linkRegistry: HateoasLinksService,
    private userSessionService: UserSessionService,
    private appInitService: AppInitService
  ) {}

  // LOGIN
  login(
    mail: string,
    password: string,
    rememberMe: boolean
  ): Observable<boolean> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + btoa(`${mail}:${password}`),
    });
    const storage = rememberMe ? localStorage : sessionStorage;

    localStorage.setItem(this.rememberMeKey, JSON.stringify(rememberMe));
    this.clearTokens();

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

          // Store tokens
          if (accessToken) {
            storage.setItem(this.authTokenKey, accessToken);
            this.userSessionService.setAccessToken(accessToken);
          }
          if (refreshToken) {
            storage.setItem(this.refreshTokenKey, refreshToken);
          }

          // 1) Load user data (if present)
          const userObs = userUrl
            ? this.http.get<UserDto>(userUrl).pipe(
                tap((userDto) => {
                  // Register links from userDto
                  if (userDto._links) {
                    this.linkRegistry.registerLinks(userDto._links, 'user:');
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
            tap(() => {
              // (Optional) If you want to auto-explore newly discovered links
              // you could add a "discovery" step here, or just declare success.
              // e.g. this.discoverAllCurrentlyRegisteredLinks();
            }),
            mergeMap(() => {
              // If everything fetched without error, we’re good
              return of(true);
            })
          );
        }),
        catchError((error) => {
          console.error('Authentication error:', error);
          return of(false);
        })
      );
  }

  // REFRESH TOKEN
  refreshToken(): Observable<boolean> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return of(false);
    }

    const url = `${this.apiServerUrl}/`; // your refresh URL
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${refreshToken}`,
    });
    const storage = this.getRememberMe() ? localStorage : sessionStorage;

    return this.http.get<any>(url, { headers, observe: 'response' }).pipe(
      tap((response) => {
        const newAccessToken = response.headers.get('X-Access-Token');
        if (!newAccessToken) {
          throw new Error('No tokens returned in refresh response');
        }
        storage.setItem(this.authTokenKey, newAccessToken);
        this.userSessionService.setAccessToken(newAccessToken);
      }),
      catchError((error) => {
        console.error('Refresh token failed:', error);
        this.logout();
        return throwError(() => error);
      }),
      mergeMap(() => of(true))
    );
  }

  // LOGOUT
  logout(): void {
    // Clear session and local storage
    this.clearTokens();
    sessionStorage.clear();
    localStorage.clear();

    // Notify other services or components
    this.userSessionService.logout();
    this.linkRegistry.clearLinks();
  }

  getAccessToken(): string {
    const storage = this.getRememberMe() ? localStorage : sessionStorage;
    return storage.getItem(this.authTokenKey) || '';
  }

  getRefreshToken(): string {
    const storage = this.getRememberMe() ? localStorage : sessionStorage;
    return storage.getItem(this.refreshTokenKey) || '';
  }

  getRememberMe(): boolean {
    return JSON.parse(localStorage.getItem(this.rememberMeKey) || 'false');
  }

  isLoggedIn(): boolean {
    return !!this.getAccessToken();
  }

  clearTokens(): void {
    sessionStorage.removeItem(this.authTokenKey);
    localStorage.removeItem(this.authTokenKey);
    sessionStorage.removeItem(this.refreshTokenKey);
    localStorage.removeItem(this.refreshTokenKey);
  }

  private extractUrl(headerValue: string | null): string | null {
    return headerValue?.split(';')[0].replace(/[<>]/g, '') ?? null;
  }

  // Example: A BFS or DFS approach to further discover new links after you’ve registered them
  /*
        private discoverAllCurrentlyRegisteredLinks(): void {
        // For advanced scenarios only:
        // 1) gather all known link URLs
        // 2) for each, if not visited, fetch and register
        // 3) repeat until no new links discovered or a time limit is reached
        }
        */
}
