import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MarketplacePageComponent } from './marketplace-page.component';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

// Import service types (adjust paths as needed)
import { ProductService, LinkKey, Product, Roles } from '@shared/index';
import { DepartmentService } from '@shared/index';
import { HateoasLinksService } from '@core/index';

describe('MarketplacePageComponent - Initialization', () => {
  let component: MarketplacePageComponent;
  let fixture: ComponentFixture<MarketplacePageComponent>;

  // Fake query parameters: page=2, size=30, inDepartment='dept1'
  const fakeQueryParams = { page: '2', size: '30', inDepartment: 'dept1' };
  const fakeActivatedRoute = { queryParams: of(fakeQueryParams) };

  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // Stub for ProductService.getProducts – returning a dummy response.
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
        userRoleEnum: Roles.ADMINISTRATOR,
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

  // DepartmentService and HateoasLinksService are not used in this test.
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
        { provide: ProductService, useValue: productServiceSpy },
        { provide: DepartmentService, useValue: departmentServiceSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: Router, useValue: routerSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      // Override the component template to a dummy one.
      .overrideTemplate(MarketplacePageComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketplacePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // Triggers ngOnInit
  });

  it('should initialize by reading query params and loading products', () => {
    // Verify that query parameters are applied.
    expect(component.page).toEqual(2);
    expect(component.size).toEqual(30);
    expect(component.selectedDepartment).toEqual('dept1');

    // Verify that getProducts was called with expected parameters.
    expect(productServiceSpy.getProducts).toHaveBeenCalledWith({
      page: 2,
      size: 30,
      inDepartment: 'dept1',
    });

    // Verify that the component stores the returned data.
    expect(component.productList).toEqual(dummyProducts);
    expect(component.totalPages).toEqual(3);
    expect(component.isLoading).toBeFalse();
  });
});
