import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
  waitForAsync,
} from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

import { MarketplaceProductEditPageComponent } from './marketplace-product-edit-page.component';
import {
  DepartmentService,
  ProductService,
  Department,
  Product,
  LinkKey,
} from '@shared/index';
import { ImageService, ToastService, HateoasLinksService } from '@core/index';
import { TranslateService } from '@ngx-translate/core';

const dummySeller = {
  email: 'seller@example.com',
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
  self: 'sellerSelf',
};

const dummyDepartments: Department[] = [
  { self: 'dept1', name: 'Dept 1', displayName: 'Department 1' },
  { self: 'dept2', name: 'Dept 2', displayName: 'Department 2' },
];

const dummyProduct: Product = {
  name: 'Test Product',
  description: 'Test Description',
  price: 50,
  used: false,
  stock: 10,
  inquiries: 'inquiriesUrl',
  totalPendingRequests: 0,
  createdAt: new Date(),
  firstImage: 'image1.jpg',
  secondImage: 'image2.jpg',
  thirdImage: null,
  seller: dummySeller,
  department: dummyDepartments[0],
  self: 'productSelf',
};

const translateServiceStub = {
  instant: (key: string) => key,
  get: (key: string) => of(key),
  onLangChange: of({ lang: 'en' }),
  onTranslationChange: of({}),
  onDefaultLangChange: of({}),
};

describe('MarketplaceProductEditPageComponent', () => {
  let component: MarketplaceProductEditPageComponent;
  let fixture: ComponentFixture<MarketplaceProductEditPageComponent>;

  const departmentServiceSpy = jasmine.createSpyObj('DepartmentService', [
    'getDepartments',
  ]);
  const productServiceSpy = jasmine.createSpyObj('ProductService', [
    'updateProduct',
  ]);
  const imageServiceSpy = jasmine.createSpyObj('ImageService', ['createImage']);
  const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);
  const hateoasLinksServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  const activatedRouteStub = {
    data: of({ product: dummyProduct }),
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [MarketplaceProductEditPageComponent],
      imports: [
        ReactiveFormsModule,
        TranslateModule.forRoot(), 
      ],
      providers: [
        FormBuilder,
        { provide: DepartmentService, useValue: departmentServiceSpy },
        { provide: ProductService, useValue: productServiceSpy },
        { provide: ImageService, useValue: imageServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: HateoasLinksService, useValue: hateoasLinksServiceSpy },
        { provide: TranslateService, useValue: translateServiceStub },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    departmentServiceSpy.getDepartments.and.returnValue(of(dummyDepartments));
    hateoasLinksServiceSpy.getLink.and.callFake((key: string) => {
      if (key === LinkKey.USER_SELF) {
        return 'userSelf';
      }
      return '';
    });

    fixture = TestBed.createComponent(MarketplaceProductEditPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); 
  });

  // Test 1: Component initialization and form population.
  it('should create the component and populate the form on init', () => {
    expect(component).toBeTruthy();
    expect(component.departmentList).toEqual(dummyDepartments);
    expect(component.product).toEqual(dummyProduct);
    const formValues = component.listingForm.value;
    expect(formValues.title).toEqual(dummyProduct.name);
    expect(formValues.price).toEqual(
      '$' + Number(dummyProduct.price).toFixed(2),
    );
    expect(formValues.description).toEqual(dummyProduct.description);
    expect(formValues.departmentId).toEqual(dummyProduct.department.self);
    expect(formValues.used).toEqual(dummyProduct.used);
    expect(formValues.quantity).toEqual(dummyProduct.stock);
    expect(component.images.length).toEqual(2);
    expect(component.images[0].preview).toEqual(dummyProduct.firstImage);
    expect(component.images[1].preview).toEqual(dummyProduct.secondImage);
  });

  // Test 2: onFileChange should add a new image (simulate file upload).
  it('should add new image on file change', fakeAsync(() => {
    const dummyFile = new File(['dummy content'], 'test.png', {
      type: 'image/png',
    });

    const inputElement = document.createElement('input');
    inputElement.type = 'file';
    const dataTransfer = new DataTransfer();
    dataTransfer.items.add(dummyFile);
    inputElement.files = dataTransfer.files; 

    const dummyResult = 'data:image/png;base64,dummy';
    class FakeFileReader {
      public onload: any;
      readAsDataURL(file: File) {
        this.onload({ target: { result: dummyResult } });
      }
    }
    spyOn(window as any, 'FileReader').and.returnValue(new FakeFileReader());

    const initialLength = component.images.length;
    const event = { target: inputElement } as unknown as Event;
    component.onFileChange(event);
    tick();

    expect(component.images.length).toEqual(initialLength + 1);
    expect(component.images[initialLength].preview).toEqual(dummyResult);
  }));

  // Test 3: removeImage should remove an image.
  it('should remove an image', () => {
    expect(component.images.length).toBe(2);
    component.removeImage(0);
    expect(component.images.length).toBe(1);
    expect(component.images[0].preview).toEqual(dummyProduct.secondImage);
  });

  // Test 4: onSubmit should set formErrors when form is valid but no images are present.
  it('should set formErrors on submit when form is valid but no images are present', () => {
    component.listingForm.patchValue({
      title: 'Valid Title',
      price: '$100.00',
      description: 'Valid Description',
      departmentId: dummyDepartments[0].self,
      used: false,
      quantity: 5,
    });
    component.product = dummyProduct;
    component.images = [];

    component.onSubmit();

    expect(component.formErrors).toEqual(
      'MARKETPLACE-PRODUCT-EDIT-PAGE.PLEASE_UPLOAD_AT_LEAST_ONE_IMAGE_FOR_THE_PRODUCT',
    );
  });
});
