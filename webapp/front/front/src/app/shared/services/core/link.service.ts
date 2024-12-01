import { Injectable } from '@angular/core';
import { Links } from '../../dtos/app-dtos';

@Injectable({
    providedIn: 'root'
})
export class HateoasLinksService {
    private storageKey = 'hateoas-links';

    constructor() {
        // Initialize links from sessionStorage if available
        const storedLinks = sessionStorage.getItem(this.storageKey);
        if (storedLinks) {
            this.links = JSON.parse(storedLinks);
        }
    }

    private links: { [key: string]: string } = {};

    /**
     * Stores a link with a specific key.
     * @param key - The key identifying the link (e.g., 'tags', 'users', etc.).
     * @param url - The URL of the link to store.
     */
    setLink(key: string, url: string): void {
        this.links[key] = url;
        this.saveLinksToSession();
        // this.logLinks()
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
        this.saveLinksToSession();
    }

    /**
     * Clears all stored links.
     */
    clearLinks(): void {
        this.links = {};
        this.saveLinksToSession();
    }

    /**
     * Logs all stored links to the console.
     */
    logLinks(): void {
        console.log('Stored HATEOAS Links:', this.links);
    }

    /**
     * Saves the current links object to sessionStorage.
     */
    private saveLinksToSession(): void {
        sessionStorage.setItem(this.storageKey, JSON.stringify(this.links));
    }
}
