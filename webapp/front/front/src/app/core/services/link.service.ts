import { Injectable } from '@angular/core';
import { LinkKey } from '@shared/index';

@Injectable({
  providedIn: 'root',
})
export class HateoasLinksService {
  private readonly STORAGE_KEY = 'hateoas-links';
  private linksMap = new Map<LinkKey, string>();

  constructor() {
    const stored = sessionStorage.getItem(this.STORAGE_KEY);
    if (stored) {
      const parsed: Record<string, string> = JSON.parse(stored);
      Object.entries(parsed).forEach(([k, v]) => {
        this.linksMap.set(k as LinkKey, v);
      });
    }
  }

  setLink(key: LinkKey, url: string): void {
    this.linksMap.set(key, url);
    this.saveToSession();
  }

  getLink(key: LinkKey): string | undefined {
    return this.linksMap.get(key);
  }

  hasLink(key: LinkKey): boolean {
    return this.linksMap.has(key);
  }

  registerLinks(links: Record<string, string>, prefix?: string): void {
    if (!links) return;

    for (const [rel, href] of Object.entries(links)) {
      if (!href) continue;

      const rawKey = prefix ? `${prefix}${rel}` : rel;
      const enumKey = this.toEnumKey(rawKey);
      if (!enumKey) {
        console.warn(`Unrecognized link relationship: ${rawKey}`);
        continue;
      }

      this.linksMap.set(enumKey, href);
    }

    this.saveToSession();
  }

  removeLink(key: LinkKey): void {
    this.linksMap.delete(key);
    this.saveToSession();
  }

  clearLinks(): void {
    // Retain only root links
    const rootLinks = new Map<LinkKey, string>(
      Array.from(this.linksMap.entries()).filter(([key]) =>
        key.startsWith('root:')
      )
    );

    // Replace linksMap with the filtered map
    this.linksMap = rootLinks;

    // Save the updated links to session storage
    this.saveToSession();
  }

  logLinks(): void {
    console.log('Current link registry:', this.linksMap);
  }

  private saveToSession(): void {
    const obj: Record<string, string> = {};
    this.linksMap.forEach((value, key) => {
      obj[key] = value;
    });
    sessionStorage.setItem(this.STORAGE_KEY, JSON.stringify(obj));
  }

  private toEnumKey(raw: string): LinkKey | null {
    const possibleKey = Object.values(LinkKey).find((v) => v === raw);
    if (possibleKey) {
      const enumKey = (
        Object.keys(LinkKey) as Array<keyof typeof LinkKey>
      ).find((k) => LinkKey[k] === raw);
      return enumKey ? LinkKey[enumKey] : null;
    }
    // Normal fallback
    return null;
  }
}
