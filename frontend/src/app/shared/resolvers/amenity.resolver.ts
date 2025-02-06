// amenity.resolver.ts
import { inject } from '@angular/core';
import {
  ResolveFn,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { AmenityService, Amenity } from '@shared/index';
import { EMPTY } from 'rxjs';
import { catchError } from 'rxjs/operators';

export const amenityResolver: ResolveFn<Amenity> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const amenityService = inject(AmenityService);

  const id = route.paramMap.get('id');
  if (!id) {
    router.navigate(['/not-found']);
    return EMPTY;
  }

  return amenityService.getAmenity(id).pipe(
    catchError(error => {
      console.error('Error loading amenity:', error);
      router.navigate(['/not-found']);
      return EMPTY;
    }),
  );
};
