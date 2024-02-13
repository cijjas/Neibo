import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {catchError, Observable, of} from "rxjs";
import {map} from "rxjs/operators";
import {User} from "../models/user";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiServerUrl = environment.apiBaseUrl;
  private authToken: string = '';
  private userUrn: string = '';

  constructor(private http: HttpClient) { }

  login(mail: string, password: string, rememberMe: boolean): Observable<boolean> {
    const requestBody = {
      mail: mail,
      password: password
    };

    return this.http.post<any>(`${this.apiServerUrl}/auth`, requestBody, { observe: 'response' })
      .pipe(
        map((response) => {
          this.userUrn = response.headers.get('X-User-URN');
          console.log('User URN:', this.userUrn);
          this.authToken = response.body.token;
          return true;
        }),
        catchError((error) => {
          console.error('Authentication error:', error);
          return of(false);
        })
      );
  }




  logout(): void {
    // Perform logout logic, clear token, and user data
    this.authToken = '';
  }

  getAuthToken(): string {
    return this.authToken;
  }

  getLoggedUserData(): Observable<any> {
    return this.http.get<any>(`${this.userUrn}`)
      .pipe(
        map((response) => {
          console.log('1.User data:', response);
          return response;
        }),
        catchError((error) => {
          console.error('User data error:', error);
          return of(null);
        })
      )

  }

  isLoggedIn(): boolean {
    return !!this.authToken;
  }
}
