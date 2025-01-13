import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService, UserSessionService } from './core';
import { PreferencesService } from '@core/services/preferences.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit, OnDestroy {
  private channel: BroadcastChannel;

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
    });
    this.channel.onmessage = (event) => {
      if (event.data.type === 'login') {
        console.log('Login event received. Refreshing page...');
        window.location.reload();
      } else if (event.data.type === 'logout') {
        console.log('Logout event received. Logging out...');
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
    // monitor changes on storage
    // If 'authToken' was removed, broadcast logout
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
    ]; // Add routes where navbar shouldn't appear

    // Extract the path without query parameters
    const currentRoute = this.router.url.split('?')[0];

    return !excludedRoutes.includes(currentRoute);
  }
}
