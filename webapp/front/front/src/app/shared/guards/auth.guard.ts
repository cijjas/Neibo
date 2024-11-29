// auth.guard.ts
import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from '../services/index.service';
import {inject} from "@angular/core";

export const authGuard: CanActivateFn = (state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  if (authService.isLoggedIn()) {
    return true;
  } else {
    return router.createUrlTree(['/login']);
  }
};
