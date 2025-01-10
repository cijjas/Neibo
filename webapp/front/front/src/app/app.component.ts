// app.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService, ThemeService } from './core';
import { TokenService } from '@core/services/token.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit, OnDestroy {
  private tabId: string | null = null;

  // app.component.ts (BroadcastChannel integration)
  private channel: BroadcastChannel;

  constructor(
    private authService: AuthService,
    private tokenService: TokenService,
    private themeService: ThemeService
  ) {
    this.channel = new BroadcastChannel('auth_channel');
  }

  ngOnInit(): void {
    this.themeService.initializeTheme();
    this.channel.onmessage = (event) => {
      if (event.data.type === 'login') {
        console.log('Login event received. Refreshing the page.');
        window.location.reload();
      }
    };
    if (!this.tokenService.getRememberMe()) {
      // Assign or retrieve the unique tab ID
      this.tabId = sessionStorage.getItem('tabId');

      if (!this.tabId) {
        this.tabId = this.generateUniqueId();
        sessionStorage.setItem('tabId', this.tabId);
        this.addTab(this.tabId);
      } else {
        console.log(`Existing tab ID: ${this.tabId}`);
      }

      this.channel.onmessage = (event) => {
        if (event.data.type === 'logout') {
          this.authService.logout();
        }
      };

      // Listen to 'beforeunload' to remove this tab from 'openTabs'
      window.addEventListener(
        'beforeunload',
        this.handleBeforeUnload.bind(this)
      );
    }

    // Listen to storage events to monitor 'openTabs' and 'authToken'
    window.addEventListener('storage', this.handleStorageEvent.bind(this));
  }

  ngOnDestroy(): void {
    if (!this.tokenService.getRememberMe() && this.tabId) {
      // Remove the tab from 'openTabs' on destruction
      this.removeTab(this.tabId);
    }

    window.removeEventListener('storage', this.handleStorageEvent.bind(this));
    window.removeEventListener(
      'beforeunload',
      this.handleBeforeUnload.bind(this)
    );

    this.channel.close();
  }

  private generateUniqueId(): string {
    // Simple unique ID generator
    return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }

  private addTab(tabId: string): void {
    const openTabs = JSON.parse(
      localStorage.getItem('openTabs') || '[]'
    ) as string[];
    openTabs.push(tabId);
    localStorage.setItem('openTabs', JSON.stringify(openTabs));
    console.log(`Tab ${tabId} added. Open tabs count: ${openTabs.length}`);
  }

  private removeTab(tabId: string): void {
    const openTabs = JSON.parse(
      localStorage.getItem('openTabs') || '[]'
    ) as string[];
    const index = openTabs.indexOf(tabId);
    if (index !== -1) {
      openTabs.splice(index, 1);
      localStorage.setItem('openTabs', JSON.stringify(openTabs));
      console.log(`Tab ${tabId} removed. Open tabs count: ${openTabs.length}`);

      if (openTabs.length === 0 && !this.tokenService.getRememberMe()) {
        console.log('No open tabs left. Clearing tokens.');
        this.tokenService.clearTokens();
        this.authService.logout();
        this.channel.postMessage({ type: 'logout' });
      }
    }
  }

  private handleStorageEvent(event: StorageEvent): void {
    if (event.key === 'openTabs') {
      const openTabs = JSON.parse(event.newValue || '[]') as string[];
      console.log(`Open tabs updated. Count: ${openTabs.length}`);

      if (openTabs.length === 0 && !this.tokenService.getRememberMe()) {
        console.log('All tabs closed. Clearing tokens.');
        this.tokenService.clearTokens();
        this.authService.logout();
      }
    }

    if (event.key === 'authToken' && event.newValue === null) {
      this.authService.logout();
      this.channel.postMessage({ type: 'logout' });
    }
  }

  private handleBeforeUnload(event: BeforeUnloadEvent): void {
    if (this.tabId) {
      // Remove this tab from 'openTabs'
      this.removeTab(this.tabId);
      // Dont uncoment this unlses you want to get logged out on refresh
      // sessionStorage.removeItem('tabId');
    }
  }
}
