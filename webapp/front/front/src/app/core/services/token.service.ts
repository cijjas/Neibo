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
  }

  public hasTokens(): boolean {
    return !!this.getAccessToken() && !!this.getRefreshToken();
  }

  public isAccessTokenExpiringSoon(): boolean {
    const token = this.getAccessToken();
    if (!token) {
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
