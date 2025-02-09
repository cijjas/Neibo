import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import {
  ProductService,
  RequestService,
  Product,
  Request,
  LinkKey,
} from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { Title } from '@angular/platform-browser';
import { AppTitleKeys } from '@shared/constants/app-titles';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-marketplace-product-requests-page',
  templateUrl: './marketplace-product-requests-page.component.html',
})
export class MarketplaceProductRequestsPageComponent implements OnInit {
  channel: string = 'SellerHub';

  product?: Product;
  requestList: Request[] = [];

  markAsSoldDialogVisible: boolean = false;
  selectedUnitsRequested: number | undefined;

  selectedBuyerId?: string;
  selectedRequestId?: string;
  selectedQuantity: number = 1;
  selectedRequesterName: string | undefined;

  page: number = 1;
  pageSize: number = 20;
  totalPages: number = 1;

  showLoader: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private linkService: HateoasLinksService,
    private requestService: RequestService,
    private titleService: Title,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe(({ product }) => {
      if (!product) {
        console.error('Product not found or failed to resolve');
        return;
      }
      this.product = product;

      const title = this.translate.instant(
        AppTitleKeys.MARKETPLACE_PRODUCT_REQUESTS_PAGE,
        {
          productName: this.product.name,
        },
      );
      this.titleService.setTitle(title);

      this.fetchRequests();
    });

    this.route.queryParams.subscribe(params => {
      this.page = params['page'] ? +params['page'] : 1;
      this.fetchRequests();
    });
  }

  private fetchRequests(): void {
    const statusUrl = this.linkService.getLink(
      LinkKey.REQUESTED_REQUEST_STATUS,
    );
    this.requestService
      .getRequests({
        page: this.page,
        size: this.pageSize,
        forProduct: this.product.self,
        withStatus: statusUrl,
      })
      .subscribe({
        next: data => {
          this.requestList = data.requests;
          this.totalPages = data.totalPages;
        },
        error: err => console.error(err),
      });
  }

  // ============ Dialog Logic ============
  openMarkAsSoldDialog(
    buyerId?: string,
    requestId?: string,
    unitsRequested?: number,
    requesterName?: string,
  ): void {
    this.selectedBuyerId = buyerId;
    this.selectedRequestId = requestId;
    this.selectedUnitsRequested = unitsRequested;
    this.selectedRequesterName = requesterName;
    this.markAsSoldDialogVisible = true;
  }

  closeMarkAsSoldDialog(): void {
    this.markAsSoldDialogVisible = false;
  }

  submitMarkAsSold(): void {
    this.showLoader = true;

    let requestStatusUrl: string = this.linkService.getLink(
      LinkKey.ACCEPTED_REQUEST_STATUS,
    );
    this.requestService
      .updateRequest(this.selectedRequestId, {
        requestStatus: requestStatusUrl,
      })
      .subscribe({
        next: () => {
          this.showLoader = false;
          this.closeMarkAsSoldDialog();
          this.fetchRequests();
        },
        error: err => {
          this.showLoader = false;
          console.error('Error accepting the request:', err);
        },
      });
  }

  submitDeclineRequest(): void {
    this.showLoader = true;

    const requestData = {
      buyerId: this.selectedBuyerId,
      requestId: this.selectedRequestId,
    };

    let requestStatusUrl: string = this.linkService.getLink(
      LinkKey.DECLINED_REQUEST_STATUS,
    );
    this.requestService
      .updateRequest(this.selectedRequestId, {
        requestStatus: requestStatusUrl,
      })
      .subscribe({
        next: () => {
          this.showLoader = false;
          this.closeMarkAsSoldDialog();
          this.fetchRequests();
        },
        error: err => {
          this.showLoader = false;
          console.error('Error declining the request:', err);
        },
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
