import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService, DepartmentService, Product } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Component({
  selector: 'app-marketplace-page',
  templateUrl: './marketplace-page.component.html',
})
export class MarketplacePageComponent implements OnInit {
  productList: Product[] = [];
  channel: string = 'Marketplace';
  selectedDepartment: string | null = null;

  page: number = 1;
  totalPages: number = 1;
  size: number = 30; 

  isLoading: boolean = true;
  placeholderItems = Array.from({ length: 20 }, (_, i) => i);

  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.page = params['page'] ? +params['page'] : 1;
      this.size = params['size'] ? +params['size'] : 20;
      this.selectedDepartment = params['inDepartment'] || null;
      this.loadProducts();
    });
  }

  private loadProducts(): void {
    this.isLoading = true;

    this.productService
      .getProducts({
        page: this.page,
        size: this.size,
        inDepartment: this.selectedDepartment,
      })
      .subscribe({
        next: data => {
          this.productList = data.products;
          this.totalPages = data.totalPages;
          this.isLoading = false;
        },
        error: err => {
          console.error(err);
          this.isLoading = false;
        },
      });
  }

  onPageChange(newPage: number): void {
    this.page = newPage;
    this.updateQueryParams();
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.page, size: this.size },
      queryParamsHandling: 'merge',
    });
  }
}
