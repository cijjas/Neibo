import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { AuthService, HateoasLinksService } from '@core/index';
import { environment } from 'environments/environment';
import { TokenService } from './token.service';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AppInitService {
  constructor(
    private http: HttpClient,
    private linkService: HateoasLinksService,
    private tokenService: TokenService,
    private authService: AuthService,
    private router: Router
  ) {}

  private readonly apiServerUrl = environment.apiBaseUrl;

  async loadInitialLinks(): Promise<void> {
    // Load tokens only if "Remember Me" is selected or tokens exist

    // Fetch the root-level links and save them to local storage
    const response = await firstValueFrom(
      this.http.get<{ _links: Record<string, string> }>(this.apiServerUrl)
    );
    this.linkService.registerLinks(response._links, 'root:');

    // TODO HACER LOGOUT SI NO REFRESH
  }
}
