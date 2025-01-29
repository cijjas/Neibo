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

export const eventResolver: ResolveFn<Event> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const eventService = inject(EventService);

  const id = route.paramMap.get('id');
  if (!id) {
    router.navigate(['/not-found']);
    return EMPTY;
  }

  return eventService.getEvent(id).pipe(
    catchError(error => {
      console.error('Error loading event:', error);
      router.navigate(['/not-found']);
      return EMPTY;
    }),
  );
};
