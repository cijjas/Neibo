// product.resolver.ts
import { inject } from '@angular/core';
import {
  ResolveFn,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { ProductService, Product } from '@shared/index';
import { EMPTY } from 'rxjs';
import { catchError } from 'rxjs/operators';

export const productResolver: ResolveFn<Product> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const productService = inject(ProductService);

  const id = route.paramMap.get('id');
  if (!id) {
    router.navigate(['/not-found']);
    return EMPTY;
  }

  return productService.getProduct(id).pipe(
    catchError(error => {
      console.error('Error loading product:', error);
      router.navigate(['/not-found']);
      return EMPTY;
    }),
  );
};
