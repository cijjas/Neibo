import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { RequestService, Department, Request, Product, LinkKey } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Component({
  selector: 'app-marketplace-dashboard-buyer-page',
  templateUrl: './marketplace-dashboard-buyer-page.component.html',
})
export class MarketplaceDashboardBuyerPageComponent implements OnInit {
  darkMode: boolean = false;

  // Pagination
  page: number = 1;
  totalPages: number = 1;
  size: number = 10;

  // Lists for requests & purchases
  requestList: Request[] = [];
  purchaseList: Request[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private linkService: HateoasLinksService,
    private requestService: RequestService
  ) { }

  /**
   * We'll use two getters to know the current mode (requests / purchases)
   * without storing an extra "channel" variable.
   */
  get isPurchases(): boolean {
    return this.route.snapshot.paramMap.get('mode') === 'purchases';
  }

  get isRequests(): boolean {
    // If it's not 'purchases', we consider it 'requests'.
    return this.route.snapshot.paramMap.get('mode') !== 'purchases';
  }

  ngOnInit(): void {
    // Subscribe to route changes
    this.route.paramMap.subscribe((params: ParamMap) => {
      const mode = params.get('mode');
      this.clearLists();

      // Load the correct list based on the current route
      if (mode === 'purchases') {
        this.loadPurchases();
      } else {
        this.loadRequests();
      }
    });

    // Also handle query params for pagination
    this.route.queryParams.subscribe((queryParams) => {
      this.page = queryParams['page'] ? +queryParams['page'] : 1;
      this.size = queryParams['size'] ? +queryParams['size'] : 10;

      // Reload data when page changes
      if (this.isPurchases) {
        this.loadPurchases();
      } else {
        this.loadRequests();
      }
    });
  }

  clearLists(): void {
    // Clear the lists before reloading new data
    this.requestList = [];
    this.purchaseList = [];
  }

  loadRequests(): void {
    const userUrl: string = this.linkService.getLink(LinkKey.USER_SELF);
    const requestStatusUrl: string = this.linkService.getLink(LinkKey.REQUESTED_REQUEST_STATUS);
    const transactionTypeUrl: string = this.linkService.getLink(LinkKey.PURCHASE_TRANSACTION_TYPE);

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
          this.requestList = data.requests;
          this.totalPages = data.totalPages;
        },
        error: (err) => console.error(err),
      });
  }

  loadPurchases(): void {
    const userUrl: string = this.linkService.getLink(LinkKey.USER_SELF);
    const requestStatusUrl: string = this.linkService.getLink(LinkKey.ACCEPTED_REQUEST_STATUS);
    const transactionTypeUrl: string = this.linkService.getLink(LinkKey.PURCHASE_TRANSACTION_TYPE);

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
          this.purchaseList = data.requests;
          this.totalPages = data.totalPages;
        },
        error: (err) => console.error(err),
      });
  }

  onPageChange(newPage: number): void {
    this.page = newPage;
    this.updateQueryParams();

    // Re-fetch based on current mode
    if (this.isPurchases) {
      this.loadPurchases();
    } else {
      this.loadRequests();
    }
  }

  updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.page, size: this.size },
      queryParamsHandling: 'merge',
    });
  }
  getProductImage(product: Product): string {
    return product?.firstImage
      ? product?.firstImage
      : 'assets/images/default-product.png';
  }
  /** Department navigation (shared by both requests and purchases) */
  goToDepartment(department: Department): void {
    this.router.navigate(['/marketplace'], {
      queryParams: { inDepartment: department.self }
    });
  }
}
