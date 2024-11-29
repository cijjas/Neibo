import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { catchError, map, Observable, of } from "rxjs";
import { User } from "../../models/index";
import { UserSessionService } from "./user-session.service";

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiServerUrl = environment.apiBaseUrl;
    private rememberMeKey = 'rememberMe';
    private authTokenKey = 'authToken';

    constructor(
        private http: HttpClient,
        private userSessionService: UserSessionService
    ) { }

    login(mail: string, password: string, rememberMe: boolean): Observable<boolean> {
        const authHeaderValue = 'Basic ' + btoa(`${mail}:${password}`);
        const headers = new HttpHeaders({ Authorization: authHeaderValue });
        const storage = rememberMe ? localStorage : sessionStorage;

        // Store "remember me" preference
        localStorage.setItem(this.rememberMeKey, JSON.stringify(rememberMe));

        return this.http.get<any>(`${this.apiServerUrl}/`, { headers, observe: 'response' })
            .pipe(
                map(response => {
                    if (response.headers.has('X-Access-Token')) {
                        const authToken = response.headers.get('X-Access-Token');
                        storage.setItem(this.authTokenKey, authToken);
                        this.userSessionService.setAuthToken(authToken);

                        const userUrl = response.headers.get('X-User-URL')?.replace(/[<>]/g, '');
                        if (userUrl) {
                            this.http.get<User>(userUrl).subscribe({
                                next: (user) => this.userSessionService.setLoggedUserInformation(user),
                                error: (error) => console.error('Error fetching user:', error),
                            });
                        }
                        return true;
                    }
                    return false;
                }),
                catchError(error => {
                    console.error('Authentication error:', error);
                    return of(false);
                })
            );
    }

    logout(): void {
        sessionStorage.clear();
        localStorage.clear();
    }

    getAuthToken(): string {
        const storageType = this.getRememberMe() ? localStorage : sessionStorage;
        return storageType.getItem(this.authTokenKey) || '';
    }

    getRememberMe(): boolean {
        return JSON.parse(localStorage.getItem(this.rememberMeKey) || 'false');
    }

    isLoggedIn(): boolean {
        return !!this.getAuthToken();
    }
}
