// user.resolver.ts
import { inject } from '@angular/core';
import {
  ResolveFn,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { UserService, User } from '@shared/index';
import { EMPTY } from 'rxjs';
import { catchError } from 'rxjs/operators';

export const userResolver: ResolveFn<User> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const userService = inject(UserService);

  const id = route.paramMap.get('id');
  if (!id) {
    router.navigate(['/not-found']);
    return EMPTY;
  }

  return userService.getUser(id).pipe(
    catchError(error => {
      console.error('Error loading user:', error);
      router.navigate(['/not-found']);
      return EMPTY;
    }),
  );
};
