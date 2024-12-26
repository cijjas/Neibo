import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ProductService, RequestService, Product, Request } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Component({
  selector: 'app-marketplace-product-requests-page',
  templateUrl: './marketplace-product-requests-page.component.html',
})
export class MarketplaceProductRequestsPageComponent implements OnInit {
  // View-related properties
  darkMode: boolean = false;
  channel: string = 'SellerHub';

  // Product & requests
  productSelf: string;
  product?: Product;
  requestList: Request[] = [];

  // Dialog state (instead of showMarkAsSoldDialog boolean)
  markAsSoldDialogVisible: boolean = false;
  selectedUnitsRequested: number | undefined;

  // For the “Respond to Request”/Mark as sold
  selectedBuyerId?: string;
  selectedRequestId?: string;
  selectedQuantity: number = 1; // default
  selectedRequesterName: string | undefined; // Holds the selected requester's name

  // Pagination
  page: number = 1;
  totalPages: number = 1;

  // Loader
  showLoader: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private linkService: HateoasLinksService,
    private productService: ProductService,
    private requestService: RequestService
  ) { }

  ngOnInit(): void {
    this.productSelf = this.route.snapshot.paramMap.get('id')!;

    this.productService.getProduct(this.productSelf).subscribe((product) => {
      this.product = product;
      // Then load requests
      this.route.queryParams.subscribe(params => {
        this.page = params['page'] ? +params['page'] : 1;
        this.fetchRequests();
      });
    });
  }

  private fetchRequests(): void {
    const statusUrl = this.linkService.getLink('neighborhood:requestedRequestStatus');
    this.requestService.getRequests({
      page: this.page,
      size: 20,
      forProduct: this.productSelf,
      withStatus: statusUrl
    }).subscribe({
      next: (data) => {
        this.requestList = data.requests;
        this.totalPages = data.totalPages;
      },
      error: (err) => console.error(err),
    });
  }

  // ============ Dialog Logic ============
  // Opening the dialog sets the boolean to true
  openMarkAsSoldDialog(buyerId?: string, requestId?: string, unitsRequested?: number, requesterName?: string): void {
    this.selectedBuyerId = buyerId;
    this.selectedRequestId = requestId;
    this.selectedUnitsRequested = unitsRequested;
    this.selectedRequesterName = requesterName;
    this.markAsSoldDialogVisible = true;
  }


  // Closing the dialog sets the boolean to false
  closeMarkAsSoldDialog(): void {
    this.markAsSoldDialogVisible = false;
  }

  // Submitting the dialog
  submitMarkAsSold(): void {
    // Show loader
    this.showLoader = true;

    let requestStatusUrl: string = this.linkService.getLink('neighborhood:acceptedRequestStatus')
    this.requestService.updateRequest(this.selectedRequestId, { requestStatus: requestStatusUrl }).subscribe({
      next: () => {
        this.showLoader = false;
        this.closeMarkAsSoldDialog();
        this.fetchRequests();
      },
      error: (err) => {
        this.showLoader = false;
        console.error('Error accepting the request:', err);
      }
    })
  }

  submitDeclineRequest(): void {
    this.showLoader = true;

    const requestData = {
      buyerId: this.selectedBuyerId,
      requestId: this.selectedRequestId
    };

    let requestStatusUrl: string = this.linkService.getLink('neighborhood:declinedRequestStatus')
    this.requestService.updateRequest(this.selectedRequestId, { requestStatus: requestStatusUrl }).subscribe({
      next: () => {
        this.showLoader = false;
        this.closeMarkAsSoldDialog();
        this.fetchRequests(); // Refresh the request list
      },
      error: (err) => {
        this.showLoader = false;
        console.error('Error declining the request:', err);
      }
    });
  }

  // ============ Pagination ============
  onPageChange(newPage: number): void {
    this.page = newPage;
    this.updateQueryParams();
  }

  updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.page },
      queryParamsHandling: 'merge',
    });
  }

  // ============ Utility ============
  createRange(n?: number): number[] {
    const limit = n || 1;
    return Array.from({ length: limit }, (_, i) => i + 1);
  }

  get integerPart(): string {
    const fullPrice = this.product?.price || 0;
    return Math.floor(fullPrice).toString();
  }

  get decimalPart(): string {
    const fullPrice = this.product?.price || 0;
    const decimals = (fullPrice % 1).toFixed(2).replace('0.', '');
    return decimals;
  }

  closeRequestDialog() {
    this.markAsSoldDialogVisible = false;
  }
}
