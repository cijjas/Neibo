// auth.guard.ts
import { CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = new AuthService(); // Create an instance of your AuthService (or inject it if possible)

  if (authService.isLoggedIn()) {
    return true;
  } else {
    // Redirect unauthenticated users to the login page
    // Note: You might want to use Router.navigate instead of window.location.href for Angular navigation
    window.location.href = '/auth';
    return false;
  }
};
