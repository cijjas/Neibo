import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { HateoasLinksService } from '@core/index';
import { firstValueFrom } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AppInitService {
  constructor(
    private http: HttpClient,
    private linkService: HateoasLinksService
  ) {}

  private readonly apiServerUrl = environment.apiBaseUrl;

  async loadInitialLinks(): Promise<void> {
    const response = await firstValueFrom(
      this.http.get<{ _links: Record<string, string> }>(this.apiServerUrl)
    );
    const links = response._links;

    // Register the root-level links
    this.linkService.registerLinks(links, 'root:');
  }
}
