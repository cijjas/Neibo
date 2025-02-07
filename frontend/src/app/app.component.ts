import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { AuthService, UserSessionService } from '@core/index';
import { PreferencesService } from '@core/services/preferences.service';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { DOCUMENT } from '@angular/common';
import { environment } from 'environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit, OnDestroy {
  private channel: BroadcastChannel;

  constructor(
    private authService: AuthService,
    private router: Router,
    @Inject(DOCUMENT) private document: Document,
  ) {
    this.channel = new BroadcastChannel('auth_channel');
    this.setFavicon(environment.deployUrl + 'favicon.ico');
  }

  ngOnInit(): void {
    // Actually use the default language
    this.channel.onmessage = event => {
      if (event.data.type === 'login') {
        window.location.reload();
      } else if (event.data.type === 'logout') {
        this.authService.logout();
      }
    };

    // Listen to storage events
    window.addEventListener('storage', this.handleStorageEvent.bind(this));
  }

  setFavicon(faviconUrl: string) {
    const link: HTMLLinkElement =
      this.document.querySelector("link[rel*='icon']") ||
      this.document.createElement('link');

    link.type = 'image/x-icon';
    link.rel = 'icon';
    link.href = faviconUrl;
    this.document.head.appendChild(link);
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
