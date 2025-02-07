import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, firstValueFrom, of } from 'rxjs';
import { AuthService, HateoasLinksService } from '@core/index';
import { environment } from '../../../environments/environment';
import { TokenService } from './token.service';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AppInitService {
  constructor(
    private http: HttpClient,
    private linkService: HateoasLinksService,
  ) {}

  private readonly apiServerUrl = environment.apiBaseUrl;

  async loadInitialLinks(): Promise<void> {
    try {
      // Attempt to fetch the root-level links
      const response = await firstValueFrom(
        this.http
          .get<{ _links: Record<string, string> }>(this.apiServerUrl)
          .pipe(
            catchError(error => {
              console.error('Failed to load initial links:', error);
              return of({ _links: {} }); // Return empty links in case of an error
            }),
          ),
      );

      // Register the links if any were successfully fetched
      this.linkService.registerLinks(response._links, 'root:');
    } catch (error) {
      console.error('Unhandled error during app initialization:', error);
    }
  }
}
