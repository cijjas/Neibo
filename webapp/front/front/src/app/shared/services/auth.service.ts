import {inject, Injectable} from "@angular/core";
import {UserService} from "./user.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authToken: string = '';
  private userData: any = {};

  login(username: string, password: string): void {
    // Perform authentication logic and obtain a JWT from the server
    // Simulating a successful login with a dummy JWT
    const dummyJwt = 'your-dummy-jwt-here';
    this.authToken = dummyJwt;

    // Fetch user data using the obtained JWT (decode if necessary)
    this.userData = {
      username: username,
      // Other user attributes
    };
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
