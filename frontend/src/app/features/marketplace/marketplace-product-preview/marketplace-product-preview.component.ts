import { Component, Input } from '@angular/core';
import { Department, Product } from '@shared/index';
import { Router } from '@angular/router';
import { environment } from 'environments/environment';
import { encodeUrlSafeBase64 } from '@shared/utils/url-safe-base64.util';

@Component({
  selector: 'app-marketplace-product-preview',
  templateUrl: './marketplace-product-preview.component.html',
})
export class MarketplaceProductPreviewComponent {
  @Input() product!: Product;

  constructor(private router: Router) {}

  onProductClick(): void {
    this.router.navigate([
      '/marketplace/products',
      encodeUrlSafeBase64(this.product.self),
    ]);
  }

  getProductImage(): string {
    return this.product?.firstImage
      ? this.product?.firstImage
      : environment.deployUrl + 'assets/images/default-product.png';
  }

  goToDepartment(department: Department): void {
    this.router.navigate(['/marketplace'], {
      queryParams: { inDepartment: department.self },
    });
  }
}
