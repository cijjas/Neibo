// event.resolver.ts
import { inject } from '@angular/core';
import {
  ResolveFn,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { EventService, Event } from '@shared/index';
import { EMPTY } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { decodeUrlSafeBase64 } from '@shared/utils/url-safe-base64.util';

export const eventResolver: ResolveFn<Event> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const eventService = inject(EventService);

  const encodedId: string | null = route.paramMap.get('id');
  if (!encodedId) {
    router.navigate(['/not-found']);
    return EMPTY;
  }

  let eventUrl: string;
  try {
    eventUrl = decodeUrlSafeBase64(encodedId);
  } catch (error) {
    console.error('Error decoding event URL:', error);
    router.navigate(['/not-found']);
    return EMPTY;
  }

  return eventService.getEvent(eventUrl).pipe(
    catchError(error => {
      console.error('Error loading event:', error);
      router.navigate(['/not-found']);
      return EMPTY;
    }),
  );
};
