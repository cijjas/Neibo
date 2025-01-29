// service-provider.resolver.ts
import { inject } from '@angular/core';
import {
  ResolveFn,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { WorkerService, Worker } from '@shared/index';
import { EMPTY } from 'rxjs';
import { catchError } from 'rxjs/operators';

export const serviceProviderResolver: ResolveFn<Worker> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const workerService = inject(WorkerService);

  const id = route.paramMap.get('id');
  if (!id) {
    router.navigate(['/not-found']);
    return EMPTY;
  }

  return workerService.getWorker(id).pipe(
    catchError(error => {
      console.error('Error loading worker:', error);
      router.navigate(['/not-found']);
      return EMPTY;
    }),
  );
};
