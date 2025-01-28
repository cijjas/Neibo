import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { HateoasLinksService } from '@core/services/link.service';
import { UserSessionService } from '@core/services/user-session.service';
import { LinkKey, Roles } from '@shared/index';

export const AuthGuard = (
  authService: AuthService,
  userSessionService: UserSessionService,
  router: Router,
  linkStorage: HateoasLinksService
) => {
  if (authService.isLoggedIn()) {
    console.log('inauthguard');
    const userRole: Roles = userSessionService.getCurrentRole();

    switch (userRole) {
      case Roles.ADMINISTRATOR:
      case Roles.NEIGHBOR:
        router.navigate(['/posts']);
        break;

      case Roles.WORKER:
        const workerUrl = linkStorage.getLink(LinkKey.USER_WORKER);
        router.navigate(['services', 'profile', workerUrl]);
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

  return true; // Allow navigation to /login if not logged in
};
