import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { HateoasLinksService } from '@core/services/link.service';
import { TokenService } from '@core/services/token.service';
import { UserSessionService } from '@core/services/user-session.service';
import { LinkKey, Role } from '@shared/index';

export const AuthGuard = () => {
  const authService = inject(AuthService);
  const userSessionService = inject(UserSessionService);
  const router = inject(Router);
  const linkStorage = inject(HateoasLinksService);
  const tokenService = inject(TokenService);
  const currentUrl = router.url;

  // Utility function to handle redirection based on role
  const redirectByRole = (role: Role) => {
    const routes = {
      [Role.ADMINISTRATOR]: ['/posts'],
      [Role.NEIGHBOR]: ['/posts'],
      [Role.UNVERIFIED_NEIGHBOR]: ['/unverified'],
      [Role.SUPER_ADMIN]: ['/super-admin'],
      [Role.REJECTED]: ['/rejected'],
      [Role.WORKER]: () => {
        const workerUrl = linkStorage.getLink(LinkKey.USER_WORKER);
        return workerUrl ? ['services', 'profiles', workerUrl] : ['/not-found'];
      },
    };

    const targetRoute = routes[role];
    if (typeof targetRoute === 'function') {
      router.navigate(targetRoute());
    } else if (targetRoute) {
      router.navigate(targetRoute);
    } else {
      router.navigate(['/not-found']);
    }
  };

  // Handle ongoing token refresh
  if (tokenService.isRefreshingToken()) {
    if (currentUrl === '/' && authService.isLoggedIn()) {
      const userRole = userSessionService.getCurrentRole();
      redirectByRole(userRole);
    }
    return false; // Skip further processing while token is being refreshed
  }

  // Handle logged-in users
  if (authService.isLoggedIn()) {
    const userRole = userSessionService.getCurrentRole();
    redirectByRole(userRole);
    return false; // Prevent navigation to public routes for logged-in users
  }

  // Allow navigation to public routes for unauthenticated users
  return true;
};
