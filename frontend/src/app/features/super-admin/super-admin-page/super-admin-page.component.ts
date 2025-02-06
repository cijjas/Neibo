import { Component } from '@angular/core';
import { AuthService } from '@core/index';

@Component({
  selector: 'app-super-admin-page',
  templateUrl: './super-admin-page.component.html',
})
export class SuperAdminPageComponent {
  constructor(private authService: AuthService) {}

  goBackToMainPage(): void {
    this.authService.logout();
  }
}
