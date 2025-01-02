import { Injectable } from "@angular/core";
import { environment } from "environments/environment";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { catchError, forkJoin, map, mergeMap, Observable, of, tap, throwError } from "rxjs";
import { NeighborhoodDto, UserDto, mapNeighborhood, mapUser } from "@shared/index";
import { HateoasLinksService, UserSessionService } from "@core/index";
import { ApiRegistry } from "./api-registry.service";

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
        private userSessionService: UserSessionService,
        private apiRegistry: ApiRegistry
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

        return this.http.get<any>(`${this.apiServerUrl}/`, { headers, observe: 'response' })
            .pipe(
                mergeMap((response) => {
                    const accessToken = response.headers.get('X-Access-Token');
                    const refreshToken = response.headers.get('X-Refresh-Token');
                    const userUrl = this.extractUrl(response.headers.get('X-User-URL'));
                    const neighborhoodUrl = this.extractUrl(response.headers.get('X-Neighborhood-URL'));
                    const workersNeighborhoodUrl = this.extractUrl(response.headers.get('X-Workers-Neighborhood-URL'));

                    if (accessToken) {
                        storage.setItem(this.authTokenKey, accessToken);
                        this.userSessionService.setAccessToken(accessToken);    // This line is unnecessary, as the getAccessToken is never utilized
                    }
                    if (refreshToken) {
                        storage.setItem(this.refreshTokenKey, refreshToken);
                    }

                    // Register Root Endpoints
                    const rootLinks = response.body._links;
                    this.registerLinks(rootLinks, 'root');

                    const userObservable = userUrl
                        ? this.http.get<UserDto>(userUrl).pipe(
                            tap((userDto) => {
                                this.registerLinks(userDto._links, 'user');
                                this.saveLinksFromDto(userDto._links, 'user'); // Soon to be deprecated
                                mapUser(this.http, userDto).subscribe({
                                    next: (user) => this.userSessionService.setUserInformation(user)
                                });
                            })
                        )
                        : of(null);

                    const neighborhoodObservable = neighborhoodUrl
                        ? this.http.get<NeighborhoodDto>(neighborhoodUrl).pipe(
                            tap((neighborhoodDto) => {
                                this.registerLinks(neighborhoodDto._links, 'neighborhood');
                                this.saveLinksFromDto(neighborhoodDto._links, 'neighborhood'); // Soon to be deprecated
                                this.userSessionService.setNeighborhoodInformation(
                                    mapNeighborhood(neighborhoodDto)
                                );
                            })
                        )
                        : of(null);

                    // should be added to the wait for links to load
                    const workersNeighborhoodObservable = workersNeighborhoodUrl
                        ? this.http.get<NeighborhoodDto>(workersNeighborhoodUrl).pipe(
                            tap((workersNeighborhoodDto) => {
                                this.registerLinks(workersNeighborhoodDto._links, 'workersNeighborhood');
                            }),
                            catchError((error) => {
                                console.error("Error fetching workers neighborhood:", error);
                                return of(null); // Continue without breaking the observable chain
                            })
                        )
                        : of(null);

                    this.apiRegistry.logLinks();
                    return forkJoin([userObservable, neighborhoodObservable, workersNeighborhoodObservable]).pipe(
                        mergeMap(() =>
                            forkJoin([
                                this.waitForLinksToLoad(),
                                this.waitForLinksToLoadFromApiRegistry()
                            ]).pipe(
                                map(([result1, result2]) => result1 && result2) // Combine the results into a single boolean
                            )
                        )
                    );
                }),
                catchError((error) => {
                    console.error('Authentication error:', error);
                    return of(false);
                })
            );
    }

    // Helper method to extract URLs from headers
    private extractUrl(headerValue: string | null): string | null {
        return headerValue?.split(';')[0].replace(/[<>]/g, '') ?? null;
    }

    private registerLinks(links: Record<string, string>, context: string): void {
        if (!links) return;

        Object.entries(links).forEach(([name, uri]) => {
            if (!uri) {
                console.warn(`Link for ${context}:${name} is invalid:`, uri);
                return;
            }

            // Store the entire URI as the template (if it includes placeholders)
            const template = uri.includes('{') ? uri : null;

            // Base URI is the part before any query params or placeholders
            const baseUri = uri.split('?')[0]; // Separate the base path before query params

            this.apiRegistry.registerEndpoint(`${context}:${name}`, baseUri, template);
        });
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
            'neighborhood:superAdministratorUserRole',
            'neighborhood:administratorUserRole',
            'neighborhood:neighborUserRole',
            'neighborhood:unverifiedNeighborUserRole',
            'neighborhood:rejectedUserRole',
            'neighborhood:workerUserRole',
            'neighborhood:verifiedWorkerRole',
            'neighborhood:unverifiedWorkerRole',
            'neighborhood:rejectedWorkerRole',
            'neighborhood:amenities',
            'neighborhood:announcements',
            'neighborhood:announcementsChannel',
            'neighborhood:affiliations',
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

    private waitForLinksToLoadFromApiRegistry(): Observable<boolean> {
        const requiredLinks = [
            'root:self',
            'root:workerPosts',
            'workersNeighborhood:amenities',
            'workersNeighborhood:channels',
            'workersNeighborhood:users',
            'workersNeighborhood:self',
            'workersNeighborhood:workers',
            'neighborhood:superAdministratorUserRole',
            'neighborhood:administratorUserRole',
            'neighborhood:neighborUserRole',
            'neighborhood:unverifiedNeighborUserRole',
            'neighborhood:rejectedUserRole',
            'neighborhood:workerUserRole',
            'neighborhood:verifiedWorkerRole',
            'neighborhood:unverifiedWorkerRole',
            'neighborhood:rejectedWorkerRole',
            'neighborhood:amenities',
            'neighborhood:announcements',
            'neighborhood:announcementsChannel',
            'neighborhood:affiliations',
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
                    !!this.apiRegistry.getEndpoint(link)
                );
                if (allLinksLoaded) {
                    this.apiRegistry.logLinks()
                    clearInterval(interval);
                    observer.next(true);
                    observer.complete();
                }
            }, 100);

            // Clean up in case the observable is unsubscribed
            return () => clearInterval(interval);
        });
    }
}
