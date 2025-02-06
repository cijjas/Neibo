import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  ComponentFixture,
  TestBed,
  waitForAsync,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { NO_ERRORS_SCHEMA, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { of, BehaviorSubject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';

import { MarketplaceProductDetailPageComponent } from './marketplace-product-detail-page.component';
import { HateoasLinksService, ToastService } from '@core/index';
import {
  ProductService,
  InquiryService,
  RequestService,
  UserService,
} from '@shared/index';

describe('MarketplaceProductDetailPageComponent', () => {
  let component: MarketplaceProductDetailPageComponent;
  let fixture: ComponentFixture<MarketplaceProductDetailPageComponent>;

  const inquiryServiceSpy = jasmine.createSpyObj('InquiryService', [
    'getInquiries',
    'createInquiry',
    'getInquiry',
    'updateInquiry',
  ]);
  const userServiceSpy = jasmine.createSpyObj('UserService', [
    'getUser',
    'updatePhoneNumber',
  ]);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
  const hateoasLinksServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);
  const productServiceSpy = jasmine.createSpyObj('ProductService', [
    'dummyMethod',
  ]);
  const requestServiceSpy = jasmine.createSpyObj('RequestService', [
    'createRequest',
  ]);
  const userSessionSpy = {};
  const departmentServiceSpy = {}; 

  const changeDetectorRefStub = { detectChanges: () => {} };

  const queryParamsSubject = new BehaviorSubject<any>({
    page: '1',
    size: '10',
  });
  const dataSubject = new BehaviorSubject<any>({
    product: {
      name: 'Test Product',
      description: 'Test Description',
      price: 100,
      used: false,
      stock: 3,
      inquiries: 'productInquiriesUrl',
      totalPendingRequests: 0,
      createdAt: new Date(),
      firstImage: 'first.jpg',
      secondImage: 'second.jpg',
      thirdImage: null,
      seller: { self: 'sellerSelf', name: 'Seller' },
      department: { self: 'deptSelf', name: 'Department' },
      self: 'productSelf',
    },
  });

  const fakeActivatedRoute = {
    data: dataSubject.asObservable(),
    queryParams: queryParamsSubject.asObservable(),
    snapshot: {
      data: { product: {} },
      queryParams: { page: '1', size: '10' },
    },
  };

  const translateServiceStub = {
    instant: (key: string) => key,
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [MarketplaceProductDetailPageComponent],
      providers: [
        { provide: ProductService, useValue: productServiceSpy },
        { provide: 'UserSessionService', useValue: userSessionSpy },
        { provide: 'DepartmentService', useValue: departmentServiceSpy },
        { provide: RequestService, useValue: requestServiceSpy },
        { provide: InquiryService, useValue: inquiryServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: Router, useValue: routerSpy },
        { provide: ChangeDetectorRef, useValue: changeDetectorRefStub },
        { provide: HateoasLinksService, useValue: hateoasLinksServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: TranslateService, useValue: translateServiceStub },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(
        MarketplaceProductDetailPageComponent,
        `<div>Dummy Template</div>`,
      )
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketplaceProductDetailPageComponent);
    component = fixture.componentInstance;

    userServiceSpy.getUser.and.returnValue(
      of({
        self: 'userSelf',
        name: 'Test User',
        email: '',
        surname: '',
        darkMode: false,
        phoneNumber: '',
        identification: 0,
        creationDate: new Date(),
        language: 'en',
        userRole: '',
        userRoleEnum: null,
        userRoleDisplay: '',
        image: '',
      }),
    );
    inquiryServiceSpy.getInquiries.and.returnValue(
      of({ inquiries: [], totalPages: 1 }),
    );
    hateoasLinksServiceSpy.getLink.and.callFake((key: string) =>
      key === 'USER_SELF' ? 'userSelf' : '',
    );
    fixture.detectChanges(); 
  });

  /**
   * 1) Initialization: Ensure that the component is created and loads product inquiries.
   */
  it('should create the component and load inquiries on init', () => {
    expect(component).toBeTruthy();
    expect(inquiryServiceSpy.getInquiries).toHaveBeenCalledWith(
      'productInquiriesUrl',
      { page: 1, size: 10 },
    );
  });

  /**
   * 2) Pagination: onPageChange should update the page, merge query params, and re-fetch inquiries.
   */
  it('should update page, merge query params, and re-fetch inquiries on onPageChange()', () => {
    routerSpy.navigate.calls.reset();

    component.onPageChange(2);

    expect(component.page).toBe(2);
    expect(routerSpy.navigate).toHaveBeenCalledWith([], {
      relativeTo: fakeActivatedRoute,
      queryParams: { page: 2, size: component.size },
      queryParamsHandling: 'merge',
    });
  });

  /**
   * 3) Inquiry submission: Should submit a valid inquiry question.
   */
  it('should submit a valid inquiry question', fakeAsync(() => {
    component.loggedUser = {
      email: 'test@example.com',
      name: 'Test User',
      surname: 'User',
      darkMode: false,
      phoneNumber: '123456789',
      identification: 12345,
      creationDate: new Date(),
      language: 'en',
      userRole: 'buyer',
      userRoleEnum: null, 
      userRoleDisplay: 'Buyer',
      image: 'test-image.jpg',
      self: 'userSelf',
    };

    dataSubject.next({
      product: {
        name: 'Test Product',
        description: 'Test Description',
        price: 100,
        used: false,
        stock: 3,
        inquiries: 'productInquiriesUrl',
        totalPendingRequests: 0,
        createdAt: new Date(),
        firstImage: 'first.jpg',
        secondImage: 'second.jpg',
        thirdImage: null,
        seller: { self: 'sellerSelf', name: 'Seller' },
        department: { self: 'deptSelf', name: 'Department' },
        self: 'productSelf',
      },
    });
    component.questionMessage = 'Is it available?';

    inquiryServiceSpy.createInquiry.and.returnValue(of('inquiryUrl'));
    inquiryServiceSpy.getInquiry.and.returnValue(
      of({
        self: 'inquiryUrl',
        inquiryMessage: 'Test question',
        responseMessage: '',
        inquiryDate: new Date(),
      }),
    );

    component.submitQuestionForm();
    tick();

    expect(inquiryServiceSpy.createInquiry).toHaveBeenCalledWith(
      'productInquiriesUrl',
      'Is it available?',
      'userSelf',
    );
    expect(component.questionMessage).toEqual('');
    expect(component.questions.length).toBeGreaterThan(0);
    expect(component.questions[0].self).toEqual('inquiryUrl');
  }));
});
