import { Component, OnInit, OnDestroy } from '@angular/core';
// import { AuthService, ThemeService, UserSessionService } from './core';
import { AuthService, UserSessionService } from './core';
import { TokenService } from '@core/services/token.service';
import { UserService } from './shared';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit, OnDestroy {
  private tabId: string | null = null;
  private channel: BroadcastChannel;

  constructor(
    private authService: AuthService,
    private userSessionService: UserSessionService,
    private userService: UserService // private themeService: ThemeService
  ) {
    this.channel = new BroadcastChannel('auth_channel');
  }

  ngOnInit(): void {
    // this.themeService.initializeTheme();
    // this.userSessionService.getCurrentUser().subscribe((user) => {
    //   if (user) {
    //     // Optionally, fetch the latest user data from the backend
    //     this.userService.getUser(user.self).subscribe((updatedUser) => {
    //       this.userSessionService.setUserInformation(updatedUser);
    //     });
    //   }
    // });
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
}
