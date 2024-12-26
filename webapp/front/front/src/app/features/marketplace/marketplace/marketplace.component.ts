import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService, DepartmentService, Product } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Component({
  selector: 'app-marketplace',
  templateUrl: './marketplace.component.html',
})
export class MarketplaceComponent implements OnInit {
  darkMode: boolean = false;
  productList: Product[] = [];
  channel: string = 'Marketplace';
  selectedDepartment: string | null = null;

  // Pagination properties
  page: number = 1;
  totalPages: number = 1;
  size: number = 30; // Default page size

  constructor(
    private productService: ProductService,
    private departmentService: DepartmentService,
    private linkService: HateoasLinksService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.linkService.logLinks();

    // Read query params for page and size
    this.route.queryParams.subscribe((params) => {
      this.page = params['page'] ? +params['page'] : 1;
      this.size = params['size'] ? +params['size'] : 20;
      this.selectedDepartment = params['inDepartment'] || null;
      this.loadProducts();
    });


  }

  private loadProducts(): void {
    const productsUrl: string = this.linkService.getLink('neighborhood:products');

    this.productService.getProducts({ page: this.page, size: this.size, inDepartment: this.selectedDepartment }).subscribe({
      next: (data) => {
        this.productList = data.products;
        this.totalPages = data.totalPages;
      },
      error: (err) => console.error(err)
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
      queryParamsHandling: 'merge'
    });
  }
}
