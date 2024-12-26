import { Injectable } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivate,
    Router,
    RouterStateSnapshot,
    UrlTree
} from '@angular/router';
import { Observable, firstValueFrom } from 'rxjs';
import { AuthService, UserSessionService } from '@core/index';

@Injectable({
    providedIn: 'root'
})
export class RoleGuard implements CanActivate {
    constructor(
        private router: Router,
        private authService: AuthService,
        private userSessionService: UserSessionService
    ) { }

    async canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ): Promise<boolean | UrlTree> {
        // 1) Check if user is logged in
        if (!this.authService.isLoggedIn()) {
            // Not logged in, redirect to login
            return this.router.createUrlTree(['/login']);
        }

        // 2) Check route data for roles required by this route
        const requiredRoles = route.data['roles'] as string[] | undefined;
        if (!requiredRoles || requiredRoles.length === 0) {
            // If no roles are specified, just allow
            return true;
        }

        // 3) Get current user role from the session service (asynchronously)
        const currentUser = await firstValueFrom(this.userSessionService.getCurrentUser());

        if (!currentUser) {
            // No user in session, redirect to login
            return this.router.createUrlTree(['/login']);
        }

        const userRole = currentUser.userRole;

        // 4) Check if the user’s role matches any required role
        const isAuthorized = requiredRoles.includes(userRole);

        if (!isAuthorized) {
            // User does not have the required roles, redirect to "Not Found"
            return this.router.createUrlTree(['/not-found']);
        }

        // 5) Authorized
        return true;
    }
}
