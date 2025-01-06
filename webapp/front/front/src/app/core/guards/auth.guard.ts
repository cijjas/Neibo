import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { UserSessionService } from '@core/services/user-session.service';

export const AuthGuard = () => {
  const authService = inject(AuthService);
  const userSessionService = inject(UserSessionService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    // Get the current user's role from the UserSessionService
    const userRole = userSessionService.getCurrentUserRole();

    // Redirect based on the role
    switch (userRole) {
      case 'ADMIN':
        router.navigate(['/admin']);
        break;
      case 'USER':
        router.navigate(['/posts']);
        break;
      case 'UNVERIFIED':
        router.navigate(['/unverified']);
        break;
      default:
        router.navigate(['/not-found']); // Fallback for undefined roles
        break;
    }

    return false; // Prevent navigation to the login page
  }

  return true; // Allow navigation to /login if not logged in
};
