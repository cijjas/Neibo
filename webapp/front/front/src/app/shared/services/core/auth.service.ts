import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { catchError, map, mergeMap, Observable, of } from "rxjs";
import { User } from "../../models/index";
import { UserSessionService } from "./user-session.service";
import { Links, NeighborhoodDto, UserDto } from "../../dtos/app-dtos";
import { mapNeighborhood, mapUser } from "../index.service";
import { HateoasLinksService } from "./link.service";

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
            map((response) => {
                // Handle Access Token
                const accessToken = response.headers.get('X-Access-Token');
                if (accessToken) {
                    storage.setItem(this.authTokenKey, accessToken);
                    this.userSessionService.setAccessToken(accessToken);
                }

                // Handle Refresh Token
                const refreshToken = response.headers.get('X-Refresh-Token');
                if (refreshToken) {
                    // Logic for refresh token handling (TODO)
                }

                // Handle User URL
                const userUrl = response.headers.get('X-User-URL')?.split(';')[0].replace(/[<>]/g, '');
                if (userUrl) {
                    this.http.get<UserDto>(userUrl).pipe(
                        mergeMap((userDto) => {
                            this.saveLinksFromDto(userDto._links, 'user'); // Save links
                            return mapUser(this.http, userDto);
                        })
                    ).subscribe({
                        next: (user) => this.userSessionService.setUserInformation(user),
                        error: (error) => console.error('Error fetching user:', error),
                    });
                }

                // Handle Neighborhood URL
                const neighborhoodUrl = response.headers.get('X-Neighborhood-URL')?.split(';')[0].replace(/[<>]/g, '');
                if (neighborhoodUrl) {
                    this.http.get<NeighborhoodDto>(neighborhoodUrl).subscribe({
                        next: (neighborhoodDto) => {
                            this.saveLinksFromDto(neighborhoodDto._links, 'neighborhood');
                            this.userSessionService.setNeighborhoodInformation(
                                mapNeighborhood(neighborhoodDto)
                            );
                        },
                        error: (error) => console.error('Error fetching neighborhood:', error),
                    });
                }

                this.hateoasLinksService.logLinks();

                return true;
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
}
