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

  private refreshingToken = false;

  constructor() {}

  setRefreshingTokenState(isRefreshing: boolean): void {
    this.refreshingToken = isRefreshing;
  }

  isRefreshingToken(): boolean {
    return this.refreshingToken;
  }

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
}
