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

    // Optional:
    // If you want to automatically follow (discover) every link that just got registered:
    // await this.discoverLinks(links);
    // But that might be too broad. See “discovery” approach below.
  }

  /**
   * Example "discovery" method.
   * If you wanted to BFS or DFS from the root, you’d do that here.
   * For brevity, this just shows the skeleton of how you might do it.
   */
  private async discoverLinks(links: Record<string, string>): Promise<void> {
    for (const [rel, href] of Object.entries(links)) {
      // Some servers don’t want you to fetch certain rels.
      // You can limit or filter if needed here.
      try {
        const resource = await firstValueFrom(this.http.get<any>(href));
        if (resource._links) {
          this.linkService.registerLinks(resource._links, `${rel}:`);
          // Recursively continue discovery from newly found links...
        }
      } catch (error) {
        console.warn(`Could not follow link ${rel} -> ${href}`, error);
      }
    }
  }
}
