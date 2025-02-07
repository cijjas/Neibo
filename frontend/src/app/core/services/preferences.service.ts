// preferences.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HateoasLinksService } from './link.service';
import { LinkKey } from '@shared/models';

@Injectable({
  providedIn: 'root',
})
export class PreferencesService {
  // Map language codes to the API language links
  public languageMap: { [key: string]: string };

  private darkModeSubject = new BehaviorSubject<boolean>(false);
  // Store the language code (for example, 'en' or 'es')
  private languageSubject = new BehaviorSubject<string>(
    localStorage.getItem('preferredLanguage') || '',
  );

  darkMode$ = this.darkModeSubject.asObservable();
  language$ = this.languageSubject.asObservable();

  constructor(private linkService: HateoasLinksService) {
    // Initialize the language map using the link service
    this.languageMap = {
      es: this.linkService.getLink(LinkKey.SPANISH_LANGUAGE),
      en: this.linkService.getLink(LinkKey.ENGLISH_LANGUAGE),
    };
  }

  // Apply dark mode
  applyDarkMode(isDarkMode: boolean): void {
    const classList = document.documentElement.classList;
    if (isDarkMode) {
      classList.add('dark-mode');
    } else {
      classList.remove('dark-mode');
    }
    this.darkModeSubject.next(isDarkMode);
  }

  // Get current dark mode
  getDarkMode(): boolean {
    return this.darkModeSubject.value;
  }

  // Apply and persist language (language is a code like 'en' or 'es')
  applyLanguage(language: string): void {
    localStorage.setItem('preferredLanguage', language);
    this.languageSubject.next(language);
  }

  // Get current language code
  getLanguage(): string {
    return this.languageSubject.value;
  }
}
