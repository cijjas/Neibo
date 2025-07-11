import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import {
  RequestService,
  Department,
  Request,
  Product,
  LinkKey,
} from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { environment } from 'environments/environment';
import { AppTitleKeys } from '@shared/constants/app-titles';
import { TranslateService } from '@ngx-translate/core';
import { Title } from '@angular/platform-browser';
import { encodeUrlSafeBase64 } from '@shared/utils/url-safe-base64.util';

@Component({
  selector: 'app-marketplace-dashboard-buyer-page',
  templateUrl: './marketplace-dashboard-buyer-page.component.html',
})
export class MarketplaceDashboardBuyerPageComponent implements OnInit {
  encodeUrlSafeBase64 = encodeUrlSafeBase64;

  page: number = 1;
  totalPages: number = 1;
  size: number = 10;

  requestList: Request[] = [];
  purchaseList: Request[] = [];

  loading: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private linkService: HateoasLinksService,
    private requestService: RequestService,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  get isPurchases(): boolean {
    return this.route.snapshot.paramMap.get('mode') === 'purchases';
  }

  get isRequests(): boolean {
    return this.route.snapshot.paramMap.get('mode') !== 'purchases';
  }

  ngOnInit(): void {
    const title = this.translate.instant(
      AppTitleKeys.MARKETPLACE_DASHBOARD_BUYER_PAGE,
    );
    this.titleService.setTitle(title);

    this.route.paramMap.subscribe((params: ParamMap) => {
      const mode = params.get('mode');
      this.clearLists();

      if (mode === 'purchases') {
        this.loadPurchases();
      } else {
        this.loadRequests();
      }
    });

    this.route.queryParams.subscribe(queryParams => {
      this.page = queryParams['page'] ? +queryParams['page'] : 1;
      this.size = queryParams['size'] ? +queryParams['size'] : 10;

      if (this.isPurchases) {
        this.loadPurchases();
      } else {
        this.loadRequests();
      }
    });
  }

  clearLists(): void {
    this.requestList = [];
    this.purchaseList = [];
  }

  loadRequests(): void {
    this.loading = true;
    const userUrl: string = this.linkService.getLink(LinkKey.USER_SELF);
    const requestStatusUrl: string = this.linkService.getLink(
      LinkKey.REQUESTED_REQUEST_STATUS,
    );
    const transactionTypeUrl: string = this.linkService.getLink(
      LinkKey.PURCHASE_TRANSACTION_TYPE,
    );

    this.requestService
      .getRequests({
        requestedBy: userUrl,
        withStatus: requestStatusUrl,
        withType: transactionTypeUrl,
        page: this.page,
        size: this.size,
      })
      .subscribe({
        next: data => {
          this.requestList = data.requests;
          this.totalPages = data.totalPages;
          this.loading = false;
        },
        error: err => {
          console.error(err);
          this.loading = false;
        },
      });
  }

  loadPurchases(): void {
    this.loading = true;
    const userUrl: string = this.linkService.getLink(LinkKey.USER_SELF);
    const requestStatusUrl: string = this.linkService.getLink(
      LinkKey.ACCEPTED_REQUEST_STATUS,
    );
    const transactionTypeUrl: string = this.linkService.getLink(
      LinkKey.PURCHASE_TRANSACTION_TYPE,
    );

    this.requestService
      .getRequests({
        requestedBy: userUrl,
        withStatus: requestStatusUrl,
        withType: transactionTypeUrl,
        page: this.page,
        size: this.size,
      })
      .subscribe({
        next: data => {
          this.purchaseList = data.requests;
          this.totalPages = data.totalPages;
          this.loading = false;
        },
        error: err => {
          console.error(err);
          this.loading = false;
        },
      });
  }

  onPageChange(newPage: number): void {
    this.page = newPage;
    this.updateQueryParams();

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
      : environment.deployUrl +
          environment.deployUrl +
          'assets/images/default-product.png';
  }
  goToDepartment(department: Department): void {
    this.router.navigate(['/marketplace'], {
      queryParams: { inDepartment: department.self },
    });
  }
}
