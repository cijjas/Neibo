import { Component, Input } from '@angular/core';
import { Department, Product } from '../../shared/models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html'
})
export class ProductCardComponent {
  @Input() product!: Product;

  constructor(
    private router: Router

  ) { }


  onProductClick(): void {
    // Navigate to product detail page

    this.router.navigate(['/marketplace/products', this.product.self]);
  }


  goToDepartment(department: Department): void {
    this.router.navigate(['/marketplace'], {
      queryParams: { inDepartment: department.self }
    });
  }

}
