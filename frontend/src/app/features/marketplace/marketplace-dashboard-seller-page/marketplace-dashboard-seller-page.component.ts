import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import {
  ProductService,
  RequestService,
  Department,
  Product,
  Request,
  LinkKey,
} from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { environment } from 'environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { Title } from '@angular/platform-browser';
import { AppTitleKeys } from '@shared/constants/app-titles';

@Component({
  selector: 'app-marketplace-dashboard-seller-page',
  templateUrl: './marketplace-dashboard-seller-page.component.html',
})
export class MarketplaceDashboardSellerPageComponent implements OnInit {
  page: number = 1;
  totalPages: number = 1;
  size: number = 10;

  listings: Product[] = [];
  sales: Request[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private linkService: HateoasLinksService,
    private requestService: RequestService,
    private productService: ProductService,
    private translate: TranslateService,
    private titleService: Title,
  ) { }

  get isListings(): boolean {
    return this.route.snapshot.paramMap.get('mode') === 'listings';
  }

  get isSales(): boolean {
    return this.route.snapshot.paramMap.get('mode') === 'sales';
  }

  ngOnInit(): void {
    const title = this.translate.instant(
      AppTitleKeys.MARKETPLACE_DASHBOARD_SELLER_PAGE,
    );
    this.titleService.setTitle(title);

    this.route.paramMap.subscribe((params: ParamMap) => {
      const mode = params.get('mode');
      this.clearLists();

      if (mode === 'sales') {
        this.loadSales();
      } else {
        this.loadListings();
      }
    });

    this.route.queryParams.subscribe(queryParams => {
      this.page = queryParams['page'] ? +queryParams['page'] : 1;
      this.size = queryParams['size'] ? +queryParams['size'] : 10;

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
    const userUrl: string = this.linkService.getLink(LinkKey.USER_SELF);
    const productStatusUrl: string = this.linkService.getLink(
      LinkKey.SELLING_PRODUCT_STATUS,
    );

    this.productService
      .getProducts({
        forUser: userUrl,
        withStatus: productStatusUrl,
        page: this.page,
        size: this.size,
      })
      .subscribe({
        next: data => {
          this.listings = data.products;
          this.totalPages = data.totalPages;
        },
        error: err => console.error(err),
      });
  }

  loadSales(): void {
    const userUrl: string = this.linkService.getLink(LinkKey.USER_SELF);
    const requestStatusUrl: string = this.linkService.getLink(
      LinkKey.ACCEPTED_REQUEST_STATUS,
    );
    const transactionTypeUrl: string = this.linkService.getLink(
      LinkKey.SALE_TRANSACTION_TYPE,
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
          this.sales = data.requests;
          this.totalPages = data.totalPages;
        },
        error: err => {
          console.error(err);
        },
      });
  }

  onPageChange(newPage: number): void {
    this.page = newPage;
    this.updateQueryParams();

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
      queryParams: { inDepartment: department.self },
    });
  }

  goToListingDetail(productId: string): void {
    this.router.navigate(['/marketplace/products', productId]);
  }

  goToRequests(productId: string): void {
    this.router.navigate(['/marketplace/products', productId, 'requests']);
  }

  getProductImage(product: Product): string {
    return product?.firstImage
      ? product?.firstImage
      : environment.deployUrl + 'assets/images/default-product.png';
  }
}
