// token.service.ts
import { Injectable } from '@angular/core';
import { UserSessionService } from './user-session.service';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  private readonly authTokenKey = 'authToken';
  private readonly refreshTokenKey = 'refreshToken';

  constructor(private userSessionService: UserSessionService) {}

  public getAccessToken(): string {
    return localStorage.getItem(this.authTokenKey) || '';
  }

  public setAccessToken(token: string): void {
    localStorage.setItem(this.authTokenKey, token);
    this.userSessionService.setAccessToken(token);
  }

  public getRefreshToken(): string {
    return localStorage.getItem(this.refreshTokenKey) || '';
  }

  public setRefreshToken(token: string): void {
    localStorage.setItem(this.refreshTokenKey, token);
  }

  public clearTokens(): void {
    localStorage.removeItem(this.authTokenKey);
    localStorage.removeItem(this.refreshTokenKey);
    console.log('Tokens have been cleared from localStorage.');
  }

  public hasTokens(): boolean {
    return !!this.getAccessToken() && !!this.getRefreshToken();
  }

  public loadSavedTokens(): void {
    const existingAccessToken = this.getAccessToken();
    if (existingAccessToken) {
      this.userSessionService.setAccessToken(existingAccessToken);
    }
  }

  public isAccessTokenExpiringSoon(): boolean {
    const token = this.getAccessToken();
    if (!token) {
      console.log('No access token found.');
      return false;
    }

    try {
      const decoded: { exp: number } = jwtDecode(token);
      const now = Math.floor(Date.now() / 1000);
      const timeRemaining = decoded.exp - now;

      console.log(`Access token expires in ${timeRemaining} seconds.`);

      return timeRemaining < 60;
    } catch (error) {
      console.error('Failed to decode access token:', error);
      return false;
    }
  }
}
