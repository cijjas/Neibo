import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { HateoasLinksService } from '@core/services/link.service';
import { TokenService } from '@core/services/token.service';
import { UserSessionService } from '@core/services/user-session.service';
import { LinkKey, Roles } from '@shared/index';

export const AuthGuard = () => {
  const authService = inject(AuthService);
  const userSessionService = inject(UserSessionService);
  const router = inject(Router);
  const linkStorage = inject(HateoasLinksService);
  const tokenService = inject(TokenService);
  const currentUrl = router.url;

  // Handle ongoing token refresh
  if (tokenService.isRefreshingToken()) {
    // Special case: If on `/` and logged in, redirect to role-specific route after refresh
    if (currentUrl === '/' && authService.isLoggedIn()) {
      const userRole: Roles = userSessionService.getCurrentRole();
      switch (userRole) {
        case Roles.ADMINISTRATOR:
        case Roles.NEIGHBOR:
          router.navigate(['/posts']);
          break;

        case Roles.WORKER:
          const workerUrl = linkStorage.getLink(LinkKey.USER_WORKER);
          if (workerUrl) {
            router.navigate(['services', 'profile', workerUrl]);
          } else {
            router.navigate(['/not-found']); // Fallback for missing worker URL
          }
          break;

        case Roles.UNVERIFIED_NEIGHBOR:
          router.navigate(['/unverified']);
          break;

        case Roles.REJECTED:
          router.navigate(['/rejected']);
          break;

        default:
          router.navigate(['/not-found']);
          break;
      }
    }
    // Skip redirection while token refresh is in progress
    return false;
  }

  // Handle logged-in users
  if (authService.isLoggedIn()) {
    // Redirect based on user role if not already handled
    const userRole: Roles = userSessionService.getCurrentRole();

    switch (userRole) {
      case Roles.ADMINISTRATOR:
      case Roles.NEIGHBOR:
        router.navigate(['/posts']);
        break;

      case Roles.WORKER:
        const workerUrl = linkStorage.getLink(LinkKey.USER_WORKER);
        if (workerUrl) {
          router.navigate(['services', 'profile', workerUrl]);
        } else {
          router.navigate(['/not-found']); // Fallback for missing worker URL
        }
        break;

      case Roles.UNVERIFIED_NEIGHBOR:
        router.navigate(['/unverified']);
        break;

      case Roles.REJECTED:
        router.navigate(['/rejected']);
        break;

      default:
        router.navigate(['/not-found']);
        break;
    }

    return false; // Prevent navigation to the login page if already logged in
  }

  // Allow navigation to login if not logged in
  return true;
};
