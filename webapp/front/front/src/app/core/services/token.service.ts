import { Injectable } from '@angular/core';
import { UserSessionService } from './user-session.service';
import { jwtDecode } from 'jwt-decode'; // Correct import usage for `jwt-decode`

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  private readonly rememberMeKey = 'rememberMe';
  private readonly authTokenKey = 'authToken';
  private readonly refreshTokenKey = 'refreshToken';

  constructor(private userSessionService: UserSessionService) {}

  /**
   * Decide whether the user chose "Remember Me" or not.
   */
  public getRememberMe(): boolean {
    return JSON.parse(localStorage.getItem(this.rememberMeKey) || 'false');
  }

  /**
   * Store the rememberMe selection in localStorage for future reference.
   */
  public setRememberMe(rememberMe: boolean): void {
    localStorage.setItem(this.rememberMeKey, JSON.stringify(rememberMe));
  }

  /**
   * Return the current Access Token from whichever storage is in use (local or session).
   */
  public getAccessToken(): string {
    // Always from localStorage so that multiple tabs share them
    return localStorage.getItem(this.authTokenKey) || '';
  }

  public setAccessToken(token: string): void {
    localStorage.setItem(this.authTokenKey, token);
    // Reflect in memory
    this.userSessionService.setAccessToken(token);
  }

  public getRefreshToken(): string {
    return localStorage.getItem(this.refreshTokenKey) || '';
  }

  public setRefreshToken(token: string): void {
    localStorage.setItem(this.refreshTokenKey, token);
  }

  public clearTokens(): void {
    sessionStorage.removeItem(this.authTokenKey);
    sessionStorage.removeItem(this.refreshTokenKey);
    localStorage.removeItem(this.authTokenKey);
    localStorage.removeItem(this.refreshTokenKey);
  }

  /**
   * Called on app startup to sync any existing tokens into memory.
   */
  public loadSavedTokens(): void {
    const existingAccessToken = this.getAccessToken();
    if (existingAccessToken) {
      this.userSessionService.setAccessToken(existingAccessToken);
    }
  }

  /**
   * Return true if token is about to expire (e.g. <60 seconds left).
   */
  public isAccessTokenExpiringSoon(): boolean {
    const token = this.getAccessToken();
    if (!token) {
      console.log('No access token found.');
      return false;
    }

    try {
      // The shape of the JWT payload can vary, but typically it has `exp`
      const decoded: { exp: number } = jwtDecode(token);

      const now = Math.floor(Date.now() / 1000); // Current time in seconds
      const timeRemaining = decoded.exp - now;

      console.log(`Access token expires in ${timeRemaining} seconds.`);

      // "Expiring soon" if <1 minute left
      return timeRemaining < 60;
    } catch (error) {
      console.error('Failed to decode access token:', error);
      return false;
    }
  }
}
