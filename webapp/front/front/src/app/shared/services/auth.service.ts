import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {catchError, Observable, of} from "rxjs";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiServerUrl = environment.apiBaseUrl;
  private authToken: string = '';
  private userData: any = {};

  constructor(private http: HttpClient) { }

  login(mail: string, password: string, rememberMe: boolean): Observable<boolean> {
    const requestBody = {
      mail: mail,
      password: password
    };

    return this.http.post<any>(`${this.apiServerUrl}/auth`, requestBody)
      .pipe(
        map((response) => {
          this.authToken = response.token;
          this.userData = this.decodeJwt(response.token);
          /*TODO handle authorization */
          console.log('User data:', this.userData);
          return true;
        }),
        catchError((error) => {
          console.error('Authentication error:', error);
          return of(false);
        })
      );
  }

  private decodeJwt(token: string): any {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map((c: string) => {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
  }

  logout(): void {
    // Perform logout logic, clear token, and user data
    this.authToken = '';
    this.userData = {};
  }

  getAuthToken(): string {
    return this.authToken;
  }

  getUserData(): any {
    return this.userData;
  }

  isLoggedIn(): boolean {
    return !!this.authToken;
  }
}
