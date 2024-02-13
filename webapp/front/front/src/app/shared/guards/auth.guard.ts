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
    window.location.href = '/login';
    return false;
  }
};
