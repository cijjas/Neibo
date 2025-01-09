import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private darkModeSubject = new BehaviorSubject<boolean>(false);
  darkMode$ = this.darkModeSubject.asObservable();

  setDarkMode(isDarkMode: boolean): void {
    this.darkModeSubject.next(isDarkMode);
    document.documentElement.classList.toggle('dark-mode', isDarkMode);
    localStorage.setItem('darkMode', JSON.stringify(isDarkMode)); // Optional: Persist preference
  }

  initializeTheme(): void {
    const storedDarkMode = JSON.parse(
      localStorage.getItem('darkMode') || 'false'
    );
    this.setDarkMode(storedDarkMode);
  }
}
