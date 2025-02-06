import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

import { MarketplaceProductRequestsPageComponent } from './marketplace-product-requests-page.component';
import {
  ProductService,
  RequestService,
  Product,
  Request,
  LinkKey,
} from '@shared/index';
import { HateoasLinksService } from '@core/index';

class ProductServiceStub {}

const dummyUser = {
  email: 'seller@test.com',
  name: 'Seller',
  surname: 'Test',
  darkMode: false,
  phoneNumber: '1234567890',
  identification: 1,
  creationDate: new Date(),
  language: 'en',
  userRole: 'seller',
  userRoleEnum: null,
  userRoleDisplay: 'Seller',
  image: 'seller.jpg',
  self: 'seller1',
};

const dummyDepartment = {
  self: 'dept1',
  name: 'Dept 1',
  displayName: 'Department 1',
};

const dummyProduct: Product = {
  name: 'Dummy Product',
  description: 'A dummy product',
  price: 100,
  used: false,
  stock: 10,
  inquiries: 'dummy-requests-url',
  totalPendingRequests: 0,
  createdAt: new Date(),
  firstImage: 'dummy1.jpg',
  secondImage: 'dummy2.jpg',
  thirdImage: null,
  seller: dummyUser,
  department: dummyDepartment,
  self: 'product1',
};

const dummyRequests: Request[] = [
  {
    message: 'Request 1',
    unitsRequested: 5,
    createdAt: new Date(),
    fulfilledAt: new Date(),
    requestStatus: 'requested',
    requestingUser: dummyUser,
    product: dummyProduct,
    self: 'req1',
  },
  {
    message: 'Request 2',
    unitsRequested: 3,
    createdAt: new Date(),
    fulfilledAt: new Date(),
    requestStatus: 'requested',
    requestingUser: dummyUser,
    product: dummyProduct,
    self: 'req2',
  },
];

describe('MarketplaceProductRequestsPageComponent', () => {
  let component: MarketplaceProductRequestsPageComponent;
  let fixture: ComponentFixture<MarketplaceProductRequestsPageComponent>;
  let routerSpy: jasmine.SpyObj<Router>;
  let activatedRouteStub: ActivatedRoute;
  let requestServiceSpy: jasmine.SpyObj<RequestService>;
  let hateoasLinksServiceSpy: jasmine.SpyObj<HateoasLinksService>;

  beforeEach(waitForAsync(() => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    requestServiceSpy = jasmine.createSpyObj('RequestService', [
      'getRequests',
      'updateRequest',
    ]);
    hateoasLinksServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
      'getLink',
    ]);

    activatedRouteStub = {
      data: of({ product: dummyProduct }),
      queryParams: of({ page: '2' }),
      snapshot: {
        data: { product: dummyProduct },
        queryParams: { page: '2' },
      },
    } as any as ActivatedRoute;

    requestServiceSpy.getRequests.and.returnValue(
      of({ requests: dummyRequests, totalPages: 5, currentPage: 2 }),
    );

    hateoasLinksServiceSpy.getLink.and.callFake((key: string) => {
      if (key === LinkKey.REQUESTED_REQUEST_STATUS) {
        return 'statusRequested';
      } else if (key === LinkKey.ACCEPTED_REQUEST_STATUS) {
        return 'statusAccepted';
      } else if (key === LinkKey.DECLINED_REQUEST_STATUS) {
        return 'statusDeclined';
      }
      return '';
    });

    TestBed.configureTestingModule({
      declarations: [MarketplaceProductRequestsPageComponent],
      imports: [
        TranslateModule.forRoot(), 
      ],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: Router, useValue: routerSpy },
        { provide: RequestService, useValue: requestServiceSpy },
        { provide: HateoasLinksService, useValue: hateoasLinksServiceSpy },
        { provide: ProductService, useClass: ProductServiceStub },
      ],
      schemas: [NO_ERRORS_SCHEMA], 
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketplaceProductRequestsPageComponent);
    component = fixture.componentInstance;
    component.productSelf = dummyProduct.self;
    fixture.detectChanges();
  });

  // Test 1: Initialization – verify product, page, and fetched requests.
  it('should initialize component with product and fetch requests', () => {
    expect(component.product).toEqual(dummyProduct);
    expect(component.page).toEqual(2);
    expect(requestServiceSpy.getRequests).toHaveBeenCalledWith({
      page: 2,
      size: 20,
      forProduct: dummyProduct.self,
      withStatus: 'statusRequested',
    });
    expect(component.requestList).toEqual(dummyRequests);
    expect(component.totalPages).toEqual(5);
  });

  // Test 2: Dialog Logic – openMarkAsSoldDialog sets properties; closeMarkAsSoldDialog hides the dialog.
  it('should open and close the mark as sold dialog', () => {
    component.openMarkAsSoldDialog('buyer1', 'req1', 10, 'John Doe');
    expect(component.selectedBuyerId).toBe('buyer1');
    expect(component.selectedRequestId).toBe('req1');
    expect(component.selectedUnitsRequested).toBe(10);
    expect(component.selectedRequesterName).toBe('John Doe');
    expect(component.markAsSoldDialogVisible).toBeTrue();

    component.closeMarkAsSoldDialog();
    expect(component.markAsSoldDialogVisible).toBeFalse();
  });

  // Test 3: Pagination – onPageChange updates the page and navigates with updated query parameters.
  it('should update page and query params on page change', () => {
    component.onPageChange(3);
    expect(component.page).toEqual(3);
    expect(routerSpy.navigate).toHaveBeenCalledWith([], {
      relativeTo: activatedRouteStub,
      queryParams: { page: 3 },
      queryParamsHandling: 'merge',
    });
  });
});
