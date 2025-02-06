import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@core/index'; 

@Component({
  selector: 'app-unverified-page',
  templateUrl: './unverified-page.component.html',
})
export class UnverifiedPageComponent {
  constructor(private router: Router, private authService: AuthService) {}

  goBackToMainPage(): void {
    this.authService.logout();
  }
}
