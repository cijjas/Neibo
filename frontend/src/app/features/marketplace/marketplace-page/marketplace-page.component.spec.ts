import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MarketplacePageComponent } from './marketplace-page.component';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { ProductService, LinkKey, Product, Role } from '@shared/index';
import { DepartmentService } from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { TranslateService } from '@ngx-translate/core';

export class FakeTranslateService {
  currentLang = 'en';
  setDefaultLang(lang: string): void {}
  use(lang: string): Observable<string> {
    this.currentLang = lang;
    return of(lang);
  }
  instant(key: string): string {
    return key;
  }
}

describe('MarketplacePageComponent - Initialization', () => {
  let component: MarketplacePageComponent;
  let fixture: ComponentFixture<MarketplacePageComponent>;

  const fakeQueryParams = { page: '2', size: '30', inDepartment: 'dept1' };
  const fakeActivatedRoute = { queryParams: of(fakeQueryParams) };

  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  const dummyProducts: Product[] = [
    {
      name: 'Product1',
      description: 'Description1',
      price: 10,
      used: false,
      stock: 100,
      inquiries: 'inquiries_link',
      totalPendingRequests: 0,
      createdAt: new Date(),
      firstImage: 'image1.png',
      secondImage: '',
      thirdImage: '',
      seller: {
        email: 'seller@example.com',
        name: 'Seller',
        surname: 'One',
        darkMode: false,
        phoneNumber: '1234567890',
        identification: 1,
        creationDate: new Date(),
        language: 'en',
        userRole: '',
        userRoleEnum: Role.ADMINISTRATOR,
        userRoleDisplay: 'Admin',
        image: 'seller_image.png',
        self: 'seller_self',
      },
      department: {
        name: 'Electronics',
        displayName: 'Electronics',
        self: 'dept1',
      },
      self: 'product1_self',
    },
  ];

  const dummyProductsResponse = {
    products: dummyProducts,
    totalPages: 3,
    currentPage: 2,
  };

  const productServiceSpy = jasmine.createSpyObj('ProductService', [
    'getProducts',
  ]);
  productServiceSpy.getProducts.and.returnValue(of(dummyProductsResponse));

  const departmentServiceSpy = jasmine.createSpyObj('DepartmentService', [
    'dummyMethod',
  ]);
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [MarketplacePageComponent],
      providers: [
        { provide: TranslateService, useClass: FakeTranslateService },

        { provide: ProductService, useValue: productServiceSpy },
        { provide: DepartmentService, useValue: departmentServiceSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: Router, useValue: routerSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(MarketplacePageComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketplacePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should initialize by reading query params and loading products', () => {
    expect(component.page).toEqual(2);
    expect(component.size).toEqual(30);
    expect(component.selectedDepartment).toEqual('dept1');

    expect(productServiceSpy.getProducts).toHaveBeenCalledWith({
      page: 2,
      size: 30,
      inDepartment: 'dept1',
    });

    expect(component.productList).toEqual(dummyProducts);
    expect(component.totalPages).toEqual(3);
    expect(component.isLoading).toBeFalse();
  });
});
