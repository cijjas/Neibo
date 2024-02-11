// auth.guard.ts
import { CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import {inject} from "@angular/core";

export const authGuard: CanActivateFn = (route, state) => {

  // Inject HttpClient into AuthService
  const authService = inject(AuthService);

  if (authService.isLoggedIn()) {
    return true;
  } else {
    // Redirect unauthenticated users to the login page
    // Note: You might want to use Router.navigate instead of window.location.href for Angular navigation
    window.location.href = '/login';
    return false;
  }
};
