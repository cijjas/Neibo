import { Injectable } from "@angular/core";
import { environment } from "environments/environment";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { catchError, forkJoin, mergeMap, Observable, of, tap, throwError } from "rxjs";
import { NeighborhoodDto, UserDto, mapNeighborhood, mapUser } from "@shared/index";
import { HateoasLinksService, UserSessionService } from "@core/index";

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiServerUrl = environment.apiBaseUrl;
    private rememberMeKey = 'rememberMe';
    private authTokenKey = 'authToken';
    private refreshTokenKey = 'refreshToken'; // NEW

    constructor(
        private http: HttpClient,
        private hateoasLinksService: HateoasLinksService,
        private userSessionService: UserSessionService
    ) { }

    /**
     * ===============================
     *            LOGIN
     * ===============================
     */
    login(mail: string, password: string, rememberMe: boolean): Observable<boolean> {
        const headers = new HttpHeaders({
            Authorization: 'Basic ' + btoa(`${mail}:${password}`),
        });
        const storage = rememberMe ? localStorage : sessionStorage;

        // Store "remember me" preference
        localStorage.setItem(this.rememberMeKey, JSON.stringify(rememberMe));

        // Clear old tokens
        this.clearTokens();

        // Attempt login
        return this.http.get<any>(`${this.apiServerUrl}/`, { headers, observe: 'response' })
            .pipe(
                mergeMap((response) => {
                    // Handle Access Token from the response headers
                    const accessToken = response.headers.get('X-Access-Token');
                    // Handle Refresh Token from the response headers (if your backend sets it)
                    const refreshToken = response.headers.get('X-Refresh-Token'); // e.g., "X-Refresh-Token"

                    if (accessToken) {
                        storage.setItem(this.authTokenKey, accessToken);
                        this.userSessionService.setAccessToken(accessToken);
                    }
                    if (refreshToken) {
                        storage.setItem(this.refreshTokenKey, refreshToken);
                        // If you want to store it in your session service, you can:
                        // this.userSessionService.setRefreshToken(refreshToken);
                    }

                    // Extract User and Neighborhood URLs
                    const userUrl = response.headers.get('X-User-URL')
                        ?.split(';')[0].replace(/[<>]/g, '');
                    const neighborhoodUrl = response.headers.get('X-Neighborhood-URL')
                        ?.split(';')[0].replace(/[<>]/g, '');

                    // Prepare Observables for user and neighborhood
                    const userObservable = userUrl
                        ? this.http.get<UserDto>(userUrl).pipe(
                            mergeMap((userDto) => {
                                this.saveLinksFromDto(userDto._links, 'user');
                                return mapUser(this.http, userDto);
                            }),
                            tap((user) => this.userSessionService.setUserInformation(user)),
                            catchError((error) => {
                                console.error('Error fetching user:', error);
                                return of(null);
                            })
                        )
                        : of(null);

                    const neighborhoodObservable = neighborhoodUrl
                        ? this.http.get<NeighborhoodDto>(neighborhoodUrl).pipe(
                            tap((neighborhoodDto) => {
                                this.saveLinksFromDto(neighborhoodDto._links, 'neighborhood');
                                this.userSessionService.setNeighborhoodInformation(
                                    mapNeighborhood(neighborhoodDto)
                                );
                            }),
                            catchError((error) => {
                                console.error('Error fetching neighborhood:', error);
                                return of(null);
                            })
                        )
                        : of(null);

                    // Wait for both Observables to complete
                    return forkJoin([userObservable, neighborhoodObservable]).pipe(
                        mergeMap(() => {
                            // Poll until all links are loaded
                            return this.waitForLinksToLoad();
                        })
                    );
                }),
                catchError((error) => {
                    console.error('Authentication error:', error);
                    return of(false);
                })
            );
    }

    /**
     * ===============================
     *         REFRESH TOKEN
     * ===============================
     */
    refreshToken(): Observable<boolean> {
        console.log("REFRESHING ACTIVATED");

        const refreshToken = this.getRefreshToken();
        if (!refreshToken) {
            // No refresh token stored; cannot refresh
            return of(false);
        }

        // Example endpoint -> adjust to your backend
        const url = `${this.apiServerUrl}/`; // Ensure endpoint is correct
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${refreshToken}` // Pass refresh token in header
        });

        // Decide which storage to use based on "remember me" or your own logic
        const storage = this.getRememberMe() ? localStorage : sessionStorage;

        return this.http.get<any>(url, { headers, observe: 'response' })
            .pipe(
                tap((response) => {
                    const newAccessToken = response.headers.get('X-Access-Token');

                    if (!newAccessToken) {
                        throw new Error('No tokens returned in refresh response');
                    }

                    // Store them
                    storage.setItem(this.authTokenKey, newAccessToken);

                    // Update your session service
                    this.userSessionService.setAccessToken(newAccessToken);
                }),
                catchError((error) => {
                    console.error('Refresh token failed:', error);
                    // Force a logout or cleanup if needed
                    this.logout();
                    return throwError(() => error);
                }),
                mergeMap(() => of(true))
            );
    }


    /**
     * ===============================
     *          LOGOUT
     * ===============================
     */
    logout(): void {
        sessionStorage.clear();
        localStorage.clear();
        // If your backend requires an explicit logout endpoint, call it here
        // this.http.post(`${this.apiServerUrl}/logout`, {}).subscribe();
    }

    /**
     * ===============================
     *       TOKEN GETTERS
     * ===============================
     */
    getAccessToken(): string {
        const storageType = this.getRememberMe() ? localStorage : sessionStorage;
        return storageType.getItem(this.authTokenKey) || '';
    }

    getRefreshToken(): string {
        const storageType = this.getRememberMe() ? localStorage : sessionStorage;
        return storageType.getItem(this.refreshTokenKey) || '';
    }

    getRememberMe(): boolean {
        return JSON.parse(localStorage.getItem(this.rememberMeKey) || 'false');
    }

    /**
     * ===============================
     *         SESSION CHECK
     * ===============================
     */
    isLoggedIn(): boolean {
        return !!this.getAccessToken();
    }

    /**
     * ===============================
     *          CLEAR TOKENS
     * ===============================
     */
    clearTokens(): void {
        sessionStorage.removeItem(this.authTokenKey);
        localStorage.removeItem(this.authTokenKey);
        sessionStorage.removeItem(this.refreshTokenKey);
        localStorage.removeItem(this.refreshTokenKey);
    }

    /**
     * ===============================
     *          HATEOAS LINKS
     * ===============================
     */
    private saveLinksFromDto(links: Partial<Record<string, string>>, namespace: string): void {
        for (const [key, value] of Object.entries(links)) {
            this.hateoasLinksService.setLink(`${namespace}:${key}`, value);
        }
    }

    private waitForLinksToLoad(): Observable<boolean> {
        const requiredLinks = [
            'neighborhood:amenities',
            'neighborhood:announcements',
            'neighborhood:announcementsChannel',
            'neighborhood:bookings',
            'neighborhood:channels',
            'neighborhood:complaints',
            'neighborhood:complaintsChannel',
            'neighborhood:departments',
            'neighborhood:contacts',
            'neighborhood:events',
            'neighborhood:feed',
            'neighborhood:feedChannel',
            'neighborhood:hotPostStatus',
            'neighborhood:images',
            'neighborhood:languageEnglish',
            'neighborhood:languageSpanish',
            'neighborhood:likes',
            'neighborhood:nonePostStatus',
            'neighborhood:posts',
            'neighborhood:postsCount',
            'neighborhood:postStatuses',
            'neighborhood:professions',
            'neighborhood:requests',
            'neighborhood:requestedRequestStatus',
            'neighborhood:acceptedRequestStatus',
            'neighborhood:declinedRequestStatus',
            'neighborhood:purchaseTransactionType',
            'neighborhood:saleTransactionType',
            'neighborhood:resources',
            'neighborhood:shifts',
            'neighborhood:self',
            'neighborhood:tags',
            'neighborhood:trendingPostStatus',
            'neighborhood:users',
            'neighborhood:workers',
            'neighborhood:products',
            'neighborhood:boughtProductStatus',
            'neighborhood:soldProductStatus',
            'neighborhood:sellingProductStatus',
            'user:bookings',
            'user:language',
            'user:likedPosts',
            'user:neighborhood',
            'user:posts',
            'user:purchases',
            'user:requests',
            'user:sales',
            'user:self',
            'user:userRole'
        ];

        return new Observable<boolean>((observer) => {
            const interval = setInterval(() => {
                const allLinksLoaded = requiredLinks.every((link) =>
                    !!this.hateoasLinksService.getLink(link)
                );
                if (allLinksLoaded) {
                    clearInterval(interval);
                    observer.next(true);
                    observer.complete();
                }
            }, 100);
        });
    }
}
