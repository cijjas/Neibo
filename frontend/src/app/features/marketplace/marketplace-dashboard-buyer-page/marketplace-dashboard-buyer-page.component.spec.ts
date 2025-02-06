import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MarketplaceDashboardBuyerPageComponent } from './marketplace-dashboard-buyer-page.component';
import { ActivatedRoute, Router } from '@angular/router';
import { HateoasLinksService } from '@core/index';
import { RequestService } from '@shared/index';
import { of, BehaviorSubject } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('MarketplaceDashboardBuyerPageComponent', () => {
  let component: MarketplaceDashboardBuyerPageComponent;
  let fixture: ComponentFixture<MarketplaceDashboardBuyerPageComponent>;

  // Service spies
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  const requestServiceSpy = jasmine.createSpyObj('RequestService', [
    'getRequests',
  ]);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // We'll simulate paramMap and queryParams using BehaviorSubjects.
  const paramMapSubject = new BehaviorSubject({ get: (key: string) => null });
  const queryParamsSubject = new BehaviorSubject<any>({});

  // Stub the activated route so paramMap and queryParams can emit our test values
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
      declarations: [MarketplaceDashboardBuyerPageComponent],
      providers: [
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: RequestService, useValue: requestServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
      ],
      // We'll skip template errors with NO_ERRORS_SCHEMA
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(
        MarketplaceDashboardBuyerPageComponent,
        `<div>Dummy Template</div>`,
      )
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketplaceDashboardBuyerPageComponent);
    component = fixture.componentInstance;

    // By default, stubs for getRequests returning an observable with no data
    requestServiceSpy.getRequests.and.returnValue(
      of({ requests: [], totalPages: 1, currentPage: 1 }),
    );
  });

  /**
   * 1) Initialization with mode = 'purchases'
   */
  it('should loadPurchases on init if route param "mode" = "purchases"', () => {
    // Make paramMap return 'purchases' for mode
    const paramMapValue = {
      get: (key: string) => (key === 'mode' ? 'purchases' : null),
    };
    paramMapSubject.next(paramMapValue);
    fakeActivatedRoute.snapshot.paramMap = paramMapValue;

    fixture.detectChanges(); // triggers ngOnInit

    expect(component.isPurchases).toBeTrue();
    expect(requestServiceSpy.getRequests).toHaveBeenCalled(); // Called by loadPurchases
    expect(component.purchaseList).toBeDefined();
    expect(component.requestList.length).toBe(0); // Cleared
  });

  /**
   * 2) Initialization with mode != 'purchases'
   */
  it('should loadRequests on init if route param "mode" != "purchases"', () => {
    const paramMapValue = {
      get: (key: string) => (key === 'mode' ? 'requests' : null),
    };
    paramMapSubject.next(paramMapValue);
    fakeActivatedRoute.snapshot.paramMap = paramMapValue;

    fixture.detectChanges(); // triggers ngOnInit

    expect(component.isRequests).toBeTrue();
    expect(requestServiceSpy.getRequests).toHaveBeenCalled(); // Called by loadRequests
    expect(component.requestList).toBeDefined();
    expect(component.purchaseList.length).toBe(0);
  });

  /**
   * 3) Pagination: calling onPageChange updates page, merges query params, and calls load method
   */
  it('should update page, merge query params, and re-fetch on onPageChange()', () => {
    // We'll simulate mode = 'purchases'
    const paramMapValue = {
      get: (key: string) => (key === 'mode' ? 'purchases' : null),
    };
    paramMapSubject.next(paramMapValue);
    fakeActivatedRoute.snapshot.paramMap = paramMapValue;

    // We'll also have an existing query param { size: '5' }
    const queryParamsValue = { size: '5' };
    queryParamsSubject.next(queryParamsValue);
    fakeActivatedRoute.snapshot.queryParams = queryParamsValue;

    fixture.detectChanges(); // triggers ngOnInit => calls loadPurchases

    // Reset spy calls to track only the new calls from onPageChange
    requestServiceSpy.getRequests.calls.reset();
    routerSpy.navigate.calls.reset();

    // Call onPageChange
    component.onPageChange(2);
    expect(component.page).toBe(2);
    // The route is 'purchases', so loadPurchases() is called
    expect(requestServiceSpy.getRequests).toHaveBeenCalled();

    // Check that router.navigate merges query params
    expect(routerSpy.navigate).toHaveBeenCalledWith([], {
      relativeTo: fakeActivatedRoute,
      queryParams: { page: 2, size: component.size },
      queryParamsHandling: 'merge',
    });
  });
});
