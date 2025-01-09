import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet, RoutesRecognized } from '@angular/router';
import { AuthService, ThemeService } from './core';
import { TokenService } from '@core/services/token.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private tokenService: TokenService,
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {
    this.themeService.initializeTheme();

    window.addEventListener('storage', (event) => {
      // If authToken is removed from localStorage in another tab
      if (event.key === 'authToken' && event.newValue === null) {
        // Then do a local logout
        this.authService.logout();
      }
    });

    // If the user has not checked remember me, then keep track of the
    // tabs open and when the last tab got closed then unload
    if (!this.tokenService.getRememberMe()) {
      // Increase tab count
      this.increaseTabCount();

      // On unload, decrease tab count
      window.addEventListener('unload', () => {
        if (!this.tokenService.getRememberMe()) {
          this.decreaseTabCount();
        }
      });
    }
  }

  // Increase the tab counter in localStorage
  private increaseTabCount() {
    const count = Number(localStorage.getItem('ephemeralTabCount') || '0');
    localStorage.setItem('ephemeralTabCount', (count + 1).toString());
  }

  // Decrease the tab counter in localStorage
  private decreaseTabCount() {
    let count = Number(localStorage.getItem('ephemeralTabCount') || '0');
    count = Math.max(0, count - 1);
    localStorage.setItem('ephemeralTabCount', count.toString());

    // If that was the last tab...
    if (count === 0) {
      // Clear ephemeral tokens
      this.tokenService.clearTokens();
      // optionally also do this.authService.logout();
      // but typically you'd just remove tokens so next tab wouldn't be able to use them.
    }
  }
}
