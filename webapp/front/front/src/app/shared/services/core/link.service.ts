import { Injectable } from '@angular/core';
import { Links } from '../../dtos/app-dtos';

@Injectable({
    providedIn: 'root'
})
export class HateoasLinksService {
    private links: { [key: string]: string } = {};

    /**
     * Stores a link with a specific key.
     * @param key - The key identifying the link (e.g., 'tags', 'users', etc.).
     * @param url - The URL of the link to store.
     */
    setLink(key: string, url: string): void {
        this.links[key] = url;
    }

    /**
     * Retrieves a stored link by its key.
     * @param key - The key identifying the link.
     * @returns The URL if found, otherwise undefined.
     */
    getLink(key: string): string | undefined {
        return this.links[key];
    }

    /**
     * Removes a link by its key.
     * @param key - The key identifying the link to remove.
     */
    removeLink(key: string): void {
        delete this.links[key];
    }

    /**
     * Clears all stored links.
     */
    clearLinks(): void {
        this.links = {};
    }

    /**
      * Logs all stored links to the console.
      */
    logLinks(): void {
        console.log('Stored HATEOAS Links:', this.links);
    }
}
