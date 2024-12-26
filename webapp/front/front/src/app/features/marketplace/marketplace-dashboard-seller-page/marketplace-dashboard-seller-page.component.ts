import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
// Replace with your actual models and services
import { ProductService, RequestService, Department, Product, Request } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Component({
  selector: 'app-marketplace-dashboard-seller-page',
  templateUrl: './marketplace-dashboard-seller-page.component.html',
})
export class MarketplaceDashboardSellerPageComponent implements OnInit {
  darkMode: boolean = false;

  // Pagination
  page: number = 1;
  totalPages: number = 1;
  size: number = 10;

  // Data for listings vs. sales
  listings: Product[] = [];
  sales: Request[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private linkService: HateoasLinksService,
    private requestService: RequestService,
    private productService: ProductService,
  ) { }

  // Computed properties for toggling UI
  get isListings(): boolean {
    return this.route.snapshot.paramMap.get('mode') === 'listings';
  }

  get isSales(): boolean {
    return this.route.snapshot.paramMap.get('mode') === 'sales';
  }

  ngOnInit(): void {
    // Listen for route changes (listings vs. sales)
    this.route.paramMap.subscribe((params: ParamMap) => {
      const mode = params.get('mode');
      this.clearLists();

      if (mode === 'sales') {
        this.loadSales();
      } else {
        // Default to listings if mode != 'sales'
        this.loadListings();
      }
    });

    // Handle query params for pagination
    this.route.queryParams.subscribe((queryParams) => {
      this.page = queryParams['page'] ? +queryParams['page'] : 1;
      this.size = queryParams['size'] ? +queryParams['size'] : 10;

      // Reload data if user changes pagination
      if (this.isSales) {
        this.loadSales();
      } else {
        this.loadListings();
      }
    });
  }

  clearLists(): void {
    this.listings = [];
    this.sales = [];
  }

  loadListings(): void {
    const userUrl: string = this.linkService.getLink('user:self');
    const productStatusUrl: string = this.linkService.getLink('neighborhood:sellingProductStatus');

    this.productService.getProducts({
      forUser: userUrl,
      withStatus: productStatusUrl,
      page: this.page,
      size: this.size,
    })
      .subscribe({
        next: (data) => {
          this.listings = data.products;
          this.totalPages = data.totalPages;
        },
        error: (err) => console.error(err),
      });
  }

  loadSales(): void {
    const userUrl: string = this.linkService.getLink('user:self');
    const requestStatusUrl: string = this.linkService.getLink('neighborhood:acceptedRequestStatus');
    const transactionTypeUrl: string = this.linkService.getLink('neighborhood:saleTransactionType');

    this.requestService
      .getRequests({
        requestedBy: userUrl,
        withStatus: requestStatusUrl,
        withType: transactionTypeUrl,
        page: this.page,
        size: this.size,
      })
      .subscribe({
        next: (data) => {
          this.sales = data.requests;
          this.totalPages = data.totalPages;
        },
        error: (err) => console.error(err),
      });
  }

  onPageChange(newPage: number): void {
    this.page = newPage;
    this.updateQueryParams();

    // Reload relevant data
    if (this.isSales) {
      this.loadSales();
    } else {
      this.loadListings();
    }
  }

  updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.page, size: this.size },
      queryParamsHandling: 'merge',
    });
  }

  goToDepartment(department: Department): void {
    this.router.navigate(['/marketplace'], {
      queryParams: { inDepartment: department.self }
    });
  }

  goToListingDetail(productId: string): void {
    this.router.navigate(['/marketplace/products', productId]);
  }

  goToRequests(productId: string): void {
    this.router.navigate(['/marketplace/products', productId, 'requests']);
  }
}
