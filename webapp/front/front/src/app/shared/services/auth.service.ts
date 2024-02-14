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

          this.getLoggedUserData();
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

  getLoggedUserData(): void {
    const match = this.userUrn.match(/\/neighborhoods\/(\d+)\/users\/(\d+)/);
    if (match && match.length === 3) {
      const neighborhoodId = +match[1];
      const userId = +match[2];

      this.userService.getUser(neighborhoodId, userId)
        .subscribe((user: User) => {
          console.log('Logged user data:', user);
        });

    } else {
      console.error('Invalid userUrn format');
    }
  }

  isLoggedIn(): boolean {
    return !!this.authToken;
  }
}
