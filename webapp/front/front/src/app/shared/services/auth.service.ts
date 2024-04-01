import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { catchError, Observable, of } from "rxjs";
import { map } from "rxjs/operators";
import { UserDto } from "../models/user";
import { LoggedInService } from "./loggedIn.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiServerUrl = environment.apiBaseUrl;
  private rememberMeKey = 'rememberMe';
  private authTokenKey = 'authToken';

  constructor(
    private http: HttpClient,
    private loggedInService: LoggedInService
  ) { }

  login(mail: string, password: string, rememberMe: boolean): Observable<boolean> {
    // Construct Basic Authorization header
    const authHeaderValue = 'Basic ' + btoa(`${mail}:${password}`);
    const headers = new HttpHeaders({
      'Authorization': authHeaderValue
    });

    // Depending on the rememberMe value, we will use localStorage or sessionStorage
    const storage = rememberMe ? localStorage : sessionStorage;
    localStorage.setItem(this.rememberMeKey, JSON.stringify(rememberMe));

    // Make a request to any protected endpoint to retrieve user information
    return this.http.get<UserDto>(`${this.apiServerUrl}/`, { headers: headers })
      .pipe(
        map(
          (userDto) => {
            // save the auth token in the storage (optional for Basic Auth)
            const authToken = authHeaderValue; // Storing the auth header as the token
            storage.setItem(this.authTokenKey, authToken);
            this.loggedInService.setAuthToken(authToken);
            this.loggedInService.setLoggedUserInformation(userDto);
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

  getAuthToken(): string {
    const storageType = this.getRememberMe() ? localStorage : sessionStorage;
    return storageType.getItem('authToken') || '';
  }

  getRememberMe(): boolean {
    return JSON.parse(localStorage.getItem('rememberMe') || sessionStorage.getItem('rememberMe') || 'false');
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('authToken') || !!sessionStorage.getItem('authToken');
  }
}
