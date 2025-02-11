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
import { decodeUrlSafeBase64 } from '@shared/utils/url-safe-base64.util';

export const amenityResolver: ResolveFn<Amenity> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const amenityService = inject(AmenityService);

  const encodedId: string | null = route.paramMap.get('id');
  if (!encodedId) {
    router.navigate(['/not-found']);
    return EMPTY;
  }

  let amenityUrl: string;
  try {
    amenityUrl = decodeUrlSafeBase64(encodedId);
  } catch (error) {
    console.error('Error decoding amenity URL:', error);
    router.navigate(['/not-found']);
    return EMPTY;
  }

  return amenityService.getAmenity(amenityUrl).pipe(
    catchError(error => {
      console.error('Error loading amenity:', error);
      router.navigate(['/not-found']);
      return EMPTY;
    }),
  );
};
