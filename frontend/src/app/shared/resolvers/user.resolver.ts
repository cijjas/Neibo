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
import { decodeUrlSafeBase64 } from '@shared/utils/url-safe-base64.util';

export const userResolver: ResolveFn<User> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const userService = inject(UserService);

  const encodedId: string | null = route.paramMap.get('id');
  if (!encodedId) {
    router.navigate(['/not-found']);
    return EMPTY;
  }

  let userUrl: string;
  try {
    userUrl = decodeUrlSafeBase64(encodedId);
  } catch (error) {
    console.error('Error decoding user URL:', error);
    router.navigate(['/not-found']);
    return EMPTY;
  }

  return userService.getUser(userUrl).pipe(
    catchError(error => {
      console.error('Error loading user:', error);
      router.navigate(['/not-found']);
      return EMPTY;
    }),
  );
};
