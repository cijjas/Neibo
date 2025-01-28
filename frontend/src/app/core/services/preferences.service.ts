import { Injectable } from '@angular/core';
import { LinkKey } from '@shared/models';
import { BehaviorSubject } from 'rxjs';
import { HateoasLinksService } from './link.service';

@Injectable({
  providedIn: 'root',
})
export class PreferencesService {
  private darkModeSubject = new BehaviorSubject<boolean>(false);
  private languageSubject = new BehaviorSubject<string>(''); // Default language

  constructor(private linkService: HateoasLinksService) {}

  darkMode$ = this.darkModeSubject.asObservable();
  language$ = this.languageSubject.asObservable();

  // Apply Dark Mode
  applyDarkMode(isDarkMode: boolean): void {
    const classList = document.documentElement.classList;
    if (isDarkMode) {
      classList.add('dark-mode');
    } else {
      classList.remove('dark-mode');
    }
    this.darkModeSubject.next(isDarkMode);
  }

  // Get Current Dark Mode
  getDarkMode(): boolean {
    return this.darkModeSubject.value;
  }

  // Apply Language
  applyLanguage(language: string): void {
    this.languageSubject.next(language);
  }

  // Get Current Language
  getLanguage(): string {
    return this.languageSubject.value;
  }
}
