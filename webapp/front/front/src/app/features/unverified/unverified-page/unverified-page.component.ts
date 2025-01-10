import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@core/index'; // Adjust to your actual auth service import

@Component({
  selector: 'app-unverified-page',
  templateUrl: './unverified-page.component.html',
})
export class UnverifiedPageComponent {
  constructor(private router: Router, private authService: AuthService) {}

  goBackToMainPage(): void {
    // Example: log out and navigate to root or login
    this.authService.logout();
  }
}
