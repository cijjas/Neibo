import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {catchError, Observable, of} from "rxjs";
import {map} from "rxjs/operators";
import {User} from "../models/user";
import {UserService} from "./user.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiServerUrl = environment.apiBaseUrl;
  private authToken: string = '';
  private userUrn: string = '';

  constructor(
    private http: HttpClient,
    private userService: UserService
  ) { }

  login(mail: string, password: string, rememberMe: boolean): Observable<boolean> {
    const requestBody = {
      mail: mail,
      password: password
    };

    return this.http.post<any>(`${this.apiServerUrl}/auth`, requestBody, { observe: 'response' })
      .pipe(
        map((response) => {
          this.userUrn = response.headers.get('X-User-URN');
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

  getLoggedUserData(): Observable<User> {
    // Assuming userUrn has the format "http://localhost:8080/neighborhoods/{neighborhoodId}/users/{userId}"
    const match = this.userUrn.match(/\/neighborhoods\/(\d+)\/users\/(\d+)/);
    if (match && match.length === 3) {
      const neighborhoodId = +match[1]; // Convert to number
      const userId = +match[2]; // Convert to number

      return this.userService.getUser(neighborhoodId, userId)
        .pipe(
          map((user) => {
            console.log('User data:', user);
            return user;
          }),
          catchError((error) => {
            console.error('User data error');
            return of(null);
          })
        );
    } else {
      console.error('Invalid userUrn format');
      return of(null);
    }
  }

  isLoggedIn(): boolean {
    return !!this.authToken;
  }
}
