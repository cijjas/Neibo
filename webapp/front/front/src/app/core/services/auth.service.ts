import { Injectable } from "@angular/core";
import { environment } from "environments/environment";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { catchError, forkJoin, mergeMap, Observable, of, tap } from "rxjs";
import { NeighborhoodDto, UserDto, mapNeighborhood, mapUser } from "@shared/index";
import { HateoasLinksService, UserSessionService } from "@core/index";

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiServerUrl = environment.apiBaseUrl;
    private rememberMeKey = 'rememberMe';
    private authTokenKey = 'authToken';

    constructor(
        private http: HttpClient,
        private hateoasLinksService: HateoasLinksService,
        private userSessionService: UserSessionService
    ) { }

    login(mail: string, password: string, rememberMe: boolean): Observable<boolean> {
        const headers = new HttpHeaders({
            Authorization: 'Basic ' + btoa(`${mail}:${password}`),
        });
        const storage = rememberMe ? localStorage : sessionStorage;

        // Store "remember me" preference
        localStorage.setItem(this.rememberMeKey, JSON.stringify(rememberMe));
        this.clearAccessToken(); // Clear old token

        return this.http.get<any>(`${this.apiServerUrl}/`, { headers, observe: 'response' }).pipe(
            mergeMap((response) => {
                // Handle Access Token
                const accessToken = response.headers.get('X-Access-Token');
                if (accessToken) {
                    storage.setItem(this.authTokenKey, accessToken);
                    this.userSessionService.setAccessToken(accessToken);
                }

                // Extract User and Neighborhood URLs
                const userUrl = response.headers.get('X-User-URL')?.split(';')[0].replace(/[<>]/g, '');
                const neighborhoodUrl = response.headers.get('X-Neighborhood-URL')?.split(';')[0].replace(/[<>]/g, '');

                // Prepare Observables for user and neighborhood
                const userObservable = userUrl ? this.http.get<UserDto>(userUrl).pipe(
                    mergeMap((userDto) => {
                        this.saveLinksFromDto(userDto._links, 'user');
                        return mapUser(this.http, userDto);
                    }),
                    tap((user) => this.userSessionService.setUserInformation(user)),
                    catchError((error) => {
                        console.error('Error fetching user:', error);
                        return of(null);
                    })
                ) : of(null);

                const neighborhoodObservable = neighborhoodUrl ? this.http.get<NeighborhoodDto>(neighborhoodUrl).pipe(
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
                ) : of(null);

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

    logout(): void {
        sessionStorage.clear();
        localStorage.clear();
    }

    getAccessToken(): string {
        const storageType = this.getRememberMe() ? localStorage : sessionStorage;
        return storageType.getItem(this.authTokenKey) || '';
    }

    getRememberMe(): boolean {
        return JSON.parse(localStorage.getItem(this.rememberMeKey) || 'false');
    }

    isLoggedIn(): boolean {
        return !!this.getAccessToken();
    }

    clearAccessToken(): void {
        sessionStorage.removeItem(this.authTokenKey);
        localStorage.removeItem(this.authTokenKey);
    }

    // Helpers
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
