import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { UserSessionService } from '@core/services/user-session.service';
import { Role } from '@shared/index';

export const RoleGuard = async (
  route: ActivatedRouteSnapshot,
): Promise<boolean | UrlTree> => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const userSessionService = inject(UserSessionService);

  // 1) Check if user is logged in
  if (!authService.isLoggedIn()) {
    // Not logged in, redirect to login
    return router.createUrlTree(['/login']);
  }

  // 2) Check route data for roles required by this route
  const requiredRoles = route.data['roles'] as Role[] | undefined; // gets the roles specified in router
  if (!requiredRoles || requiredRoles.length === 0) {
    // If no roles are specified, allow access
    return true;
  }

  // 3) Get current user role from UserSessionService (asynchronously)
  const currentUser = userSessionService.getCurrentUser();
  if (!currentUser) {
    // No user in session, redirect to login
    return router.createUrlTree(['/login']);
  }

  const userRole = userSessionService.getCurrentRole();
  // 4) Check if the users role matches any required role
  if (requiredRoles.includes(userRole)) {
    // Authorized
    return true;
  }

  // 5) Unauthorized, redirect to "Not Found"
  return router.createUrlTree(['/not-found']);
};
