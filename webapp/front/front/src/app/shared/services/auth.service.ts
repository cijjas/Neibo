import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiServerUrl = environment.apiBaseUrl;
  private authToken: string = '';
  private userData: any = {};

  constructor(private http: HttpClient) { }

  login(mail: string, password: string): void {
    // Formulate the request body with user credentials
    const requestBody = {
      mail: mail,
      password: password
    };
    console.log('Trying to login with username: ' + mail + ' and password: ' + password);

    // Make an HTTP POST request to the authentication endpoint
    this.http.post<any>(`${this.apiServerUrl}/auth`, requestBody)
      .subscribe(
      (response) => {
        this.userData = this.decodeJwt(response.token);
        console.log('User data:', this.userData); /*TODO fijarse el payload */
      },
      (error) => {
        // Handle authentication error (e.g., display an error message)
        console.error('Authentication error:', error);
      }
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
    // Check if the user is logged in based on the presence of the JWT
    return !!this.authToken;
  }
}
