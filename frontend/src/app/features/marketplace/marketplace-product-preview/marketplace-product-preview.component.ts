import { Component, Input } from '@angular/core';
import { Department, Product } from '@shared/index';
import { Router } from '@angular/router';
import { environment } from 'environments/environment';

@Component({
  selector: 'app-marketplace-product-preview',
  templateUrl: './marketplace-product-preview.component.html'
})
export class MarketplaceProductPreviewComponent {
  @Input() product!: Product;

  constructor(
    private router: Router

  ) { }


  onProductClick(): void {
    // Navigate to product detail page

    this.router.navigate(['/marketplace/products', this.product.self]);
  }

  getProductImage(): string {
    return this.product?.firstImage
      ? this.product?.firstImage
      : environment.deployUrl + 'assets/images/default-product.png';
  }


  goToDepartment(department: Department): void {
    this.router.navigate(['/marketplace'], {
      queryParams: { inDepartment: department.self }
    });
  }

}
