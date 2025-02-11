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
import { decodeUrlSafeBase64 } from '@shared/utils/url-safe-base64.util';

export const productResolver: ResolveFn<Product> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const productService = inject(ProductService);

  const encodedId: string | null = route.paramMap.get('id');
  if (!encodedId) {
    router.navigate(['/not-found']);
    return EMPTY;
  }

  let productUrl: string;
  try {
    productUrl = decodeUrlSafeBase64(encodedId);
  } catch (error) {
    console.error('Error decoding product URL:', error);
    router.navigate(['/not-found']);
    return EMPTY;
  }

  return productService.getProduct(productUrl).pipe(
    catchError(error => {
      console.error('Error loading product:', error);
      router.navigate(['/not-found']);
      return EMPTY;
    }),
  );
};
