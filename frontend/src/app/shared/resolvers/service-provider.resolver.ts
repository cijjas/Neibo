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
import { decodeUrlSafeBase64 } from '@shared/utils/url-safe-base64.util';

export const serviceProviderResolver: ResolveFn<Worker> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const workerService = inject(WorkerService);

  const encodedId: string | null = route.paramMap.get('id');
  if (!encodedId) {
    router.navigate(['/not-found']);
    return EMPTY;
  }

  let workerUrl: string;
  try {
    workerUrl = decodeUrlSafeBase64(encodedId);
  } catch (error) {
    console.error('Error decoding worker URL:', error);
    router.navigate(['/not-found']);
    return EMPTY;
  }

  return workerService.getWorker(workerUrl).pipe(
    catchError(error => {
      console.error('Error loading worker:', error);
      router.navigate(['/not-found']);
      return EMPTY;
    }),
  );
};
