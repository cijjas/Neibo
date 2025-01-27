import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService, UserSessionService } from '@core/index';
import { PreferencesService } from '@core/services/preferences.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit, OnDestroy {
  private channel: BroadcastChannel;
  isInitialized = false;

  constructor(
    private authService: AuthService,
    private userSessionService: UserSessionService,
    private preferencesService: PreferencesService,
    private router: Router
  ) {
    this.channel = new BroadcastChannel('auth_channel');
  }

  ngOnInit(): void {
    this.userSessionService.getCurrentUser().subscribe((user) => {
      if (user) {
        this.preferencesService.applyDarkMode(user.darkMode); // Re-apply the theme
      }
      // Mark as initialized after user session check is complete
      this.isInitialized = true;
    });

    this.channel.onmessage = (event) => {
      if (event.data.type === 'login') {
        window.location.reload();
      } else if (event.data.type === 'logout') {
        this.authService.logout();
      }
    };

    // Listen to storage events
    window.addEventListener('storage', this.handleStorageEvent.bind(this));
  }

  ngOnDestroy(): void {
    window.removeEventListener('storage', this.handleStorageEvent.bind(this));
    this.channel.close();
  }

  private handleStorageEvent(event: StorageEvent): void {
    if (event.key === 'authToken' && event.newValue === null) {
      this.authService.logout();
      this.channel.postMessage({ type: 'logout' });
    }
  }

  shouldShowNavbar(): boolean {
    const excludedRoutes = [
      '/login',
      '/signup',
      '/unverified',
      '/rejected',
      '/not-found',
    ];

    const currentRoute = this.router.url.split('?')[0];
    return !excludedRoutes.includes(currentRoute);
  }
}
