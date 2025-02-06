import { Component } from '@angular/core';
import { AuthService } from '@core/index';

@Component({
  selector: 'app-super-admin-page',
  templateUrl: './super-admin-page.component.html',
})
export class SuperAdminPageComponent {
  constructor(private authService: AuthService) {}

  goBackToMainPage(): void {
    // Example: log out and navigate to root or login

    this.authService.logout();
  }
}
