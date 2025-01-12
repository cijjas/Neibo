import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PreferencesService {
  private darkModeSubject = new BehaviorSubject<boolean>(false);
  darkMode$ = this.darkModeSubject.asObservable();

  applyDarkMode(isDarkMode: boolean): void {
    const classList = document.documentElement.classList;
    if (isDarkMode) {
      classList.add('dark-mode');
    } else {
      classList.remove('dark-mode');
    }
    this.darkModeSubject.next(isDarkMode);
  }
}
