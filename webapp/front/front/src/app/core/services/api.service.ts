import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root',
})
export class ApiService {
    private links: any = {};

    // Set links dynamically from API responses
    setLinks(newLinks: any): void {
        this.links = { ...this.links, ...newLinks };
    }

    // Get a specific link
    getLink(key: string): string | null {
        return this.links[key] || null;
    }
}
