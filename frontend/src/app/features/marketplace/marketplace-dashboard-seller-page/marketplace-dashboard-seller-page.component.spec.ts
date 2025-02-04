import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MarketplaceDashboardSellerPageComponent } from './marketplace-dashboard-seller-page.component';
import { ActivatedRoute, Router } from '@angular/router';
import { HateoasLinksService } from '@core/index';
import { ProductService, RequestService } from '@shared/index';
import { of, BehaviorSubject } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('MarketplaceDashboardSellerPageComponent', () => {
  let component: MarketplaceDashboardSellerPageComponent;
  let fixture: ComponentFixture<MarketplaceDashboardSellerPageComponent>;

  // Service spies
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  const requestServiceSpy = jasmine.createSpyObj('RequestService', [
    'getRequests',
  ]);
  const productServiceSpy = jasmine.createSpyObj('ProductService', [
    'getProducts',
  ]);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // We'll emit paramMap and queryParams using BehaviorSubjects.
  const paramMapSubject = new BehaviorSubject({ get: (key: string) => null });
  const queryParamsSubject = new BehaviorSubject<any>({});

  // Fake ActivatedRoute that uses the BehaviorSubject observables
  const fakeActivatedRoute = {
    paramMap: paramMapSubject.asObservable(),
    queryParams: queryParamsSubject.asObservable(),
    snapshot: {
      paramMap: { get: (key: string) => null },
      queryParams: {},
    },
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [MarketplaceDashboardSellerPageComponent],
      providers: [
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: RequestService, useValue: requestServiceSpy },
        { provide: ProductService, useValue: productServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      // Override the component template to avoid processing the real one (with unknown elements/pipes)
      .overrideTemplate(
        MarketplaceDashboardSellerPageComponent,
        `<div>Dummy Template</div>`,
      )
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketplaceDashboardSellerPageComponent);
    component = fixture.componentInstance;

    // Stubs: getRequests & getProducts return empty arrays by default
    requestServiceSpy.getRequests.and.returnValue(
      of({ requests: [], totalPages: 1, currentPage: 1 }),
    );
    productServiceSpy.getProducts.and.returnValue(
      of({ products: [], totalPages: 1, currentPage: 1 }),
    );
  });

  /**
   * 1) Initialization: mode = 'sales' => calls loadSales
   */
  it('should call loadSales on init if route param "mode" = "sales"', () => {
    // Make paramMap return 'sales'
    const paramMapValue = {
      get: (key: string) => (key === 'mode' ? 'sales' : null),
    };
    paramMapSubject.next(paramMapValue);
    fakeActivatedRoute.snapshot.paramMap = paramMapValue;

    fixture.detectChanges(); // triggers ngOnInit

    expect(component.isSales).toBeTrue();
    expect(requestServiceSpy.getRequests).toHaveBeenCalled(); // loadSales is called
    expect(component.sales).toBeDefined();
    expect(component.listings.length).toEqual(0); // We cleared listings
  });

  /**
   * 2) Initialization: mode != 'sales' => default to 'listings'
   */
  it('should call loadListings on init if route param "mode" != "sales"', () => {
    // Suppose paramMapValue = { get: (key: string) => (key === 'mode' ? 'listings' : null) };
    const paramMapValue = {
      get: (key: string) => (key === 'mode' ? 'listings' : null),
    };
    paramMapSubject.next(paramMapValue);
    fakeActivatedRoute.snapshot.paramMap = paramMapValue;

    fixture.detectChanges(); // triggers ngOnInit

    expect(component.isListings).toBeTrue();
    expect(productServiceSpy.getProducts).toHaveBeenCalled(); // loadListings is called
    expect(component.listings).toBeDefined();
    expect(component.sales.length).toEqual(0); // Cleared
  });

  /**
   * 3) Pagination: onPageChange
   */
  it('should update page, merge query params, and re-fetch based on mode in onPageChange()', () => {
    // Simulate paramMap => 'sales'
    const paramMapValue = {
      get: (key: string) => (key === 'mode' ? 'sales' : null),
    };
    paramMapSubject.next(paramMapValue);
    fakeActivatedRoute.snapshot.paramMap = paramMapValue;

    // Suppose queryParams => { size: '5' }
    const queryParamsValue = { size: '5' };
    queryParamsSubject.next(queryParamsValue);
    fakeActivatedRoute.snapshot.queryParams = queryParamsValue;

    fixture.detectChanges(); // triggers ngOnInit => calls loadSales

    // Reset spy calls so we track only the new calls from onPageChange
    requestServiceSpy.getRequests.calls.reset();
    productServiceSpy.getProducts.calls.reset();
    routerSpy.navigate.calls.reset();

    // onPageChange(2)
    component.onPageChange(2);
    expect(component.page).toBe(2);
    // Because mode = 'sales', we should call loadSales => requestService.getRequests
    expect(requestServiceSpy.getRequests).toHaveBeenCalled();
    expect(productServiceSpy.getProducts).not.toHaveBeenCalled();

    // Check that query params are merged
    expect(routerSpy.navigate).toHaveBeenCalledWith([], {
      relativeTo: fakeActivatedRoute,
      queryParams: { page: 2, size: component.size },
      queryParamsHandling: 'merge',
    });
  });
});
