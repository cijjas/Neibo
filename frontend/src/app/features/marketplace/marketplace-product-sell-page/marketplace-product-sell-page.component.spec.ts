import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
  waitForAsync,
} from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { Pipe, PipeTransform, NO_ERRORS_SCHEMA } from '@angular/core';

import { MarketplaceProductSellPageComponent } from './marketplace-product-sell-page.component';
import { ProductService, DepartmentService } from '@shared/index';
import {
  UserSessionService,
  ImageService,
  ToastService,
  HateoasLinksService,
} from '@core/index';
import { TranslateService } from '@ngx-translate/core';

@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

const fakeTranslateService = {
  instant: (key: string) => key,
  get: (key: string) => of(key),
};

const dummyDepartments = [
  { self: 'dept1', name: 'Dept 1', displayName: 'Department 1' },
  { self: 'dept2', name: 'Dept 2', displayName: 'Department 2' },
];

describe('MarketplaceProductSellPageComponent', () => {
  let component: MarketplaceProductSellPageComponent;
  let fixture: ComponentFixture<MarketplaceProductSellPageComponent>;
  let routerSpy: jasmine.SpyObj<Router>;
  let departmentServiceSpy: jasmine.SpyObj<DepartmentService>;
  let imageServiceSpy: jasmine.SpyObj<ImageService>;
  let toastServiceSpy: jasmine.SpyObj<ToastService>;
  let linkServiceSpy: jasmine.SpyObj<HateoasLinksService>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let userSessionServiceStub: Partial<UserSessionService>;

  beforeEach(waitForAsync(() => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    departmentServiceSpy = jasmine.createSpyObj('DepartmentService', [
      'getDepartments',
    ]);
    imageServiceSpy = jasmine.createSpyObj('ImageService', ['createImage']);
    toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);
    linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', ['getLink']);
    productServiceSpy = jasmine.createSpyObj('ProductService', [
      'createProduct',
    ]);
    userSessionServiceStub = {};

    departmentServiceSpy.getDepartments.and.returnValue(of(dummyDepartments));
    linkServiceSpy.getLink.and.callFake((key: string) => {
      if (key === 'USER_SELF') {
        return 'userSelf';
      }
      return '';
    });

    TestBed.configureTestingModule({
      declarations: [MarketplaceProductSellPageComponent, FakeTranslatePipe],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: ProductService, useValue: productServiceSpy },
        { provide: DepartmentService, useValue: departmentServiceSpy },
        { provide: ImageService, useValue: imageServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: TranslateService, useValue: fakeTranslateService },
        { provide: UserSessionService, useValue: userSessionServiceStub },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketplaceProductSellPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Test 1: Initialization – the component should fetch and set the department list.
  it('should fetch departments on init', () => {
    expect(component.departmentList).toEqual(dummyDepartments);
  });

  // Test 2: onFileChange – when a valid image file is selected, it should be read and added.
  it('should add new image on file change', fakeAsync(() => {
    const dummyFile = new File(['dummy content'], 'test.png', {
      type: 'image/png',
    });
    const dataTransfer = new DataTransfer();
    dataTransfer.items.add(dummyFile);
    const inputElement = document.createElement('input');
    inputElement.type = 'file';
    inputElement.files = dataTransfer.files;

    const dummyResult = 'data:image/png;base64,dummy';
    class FakeFileReader {
      public onload: any;
      readAsDataURL(file: File) {
        this.onload({ target: { result: dummyResult } });
      }
    }
    spyOn(window as any, 'FileReader').and.returnValue(new FakeFileReader());

    const event = { target: inputElement } as unknown as Event;
    component.onFileChange(event);
    tick();

    expect(component.images.length).toBeGreaterThan(0);
    expect(component.images[0].preview).toEqual(dummyResult);
  }));

  // Test 3: onSubmit – if the form is valid but no images exist, it should set formErrors.
  it('should set formErrors on submit when no images exist', () => {
    component.listingForm.patchValue({
      title: 'Test Title',
      price: '$100.00',
      description: 'Test description',
      departmentId: 'dept1',
      used: false,
      quantity: 10,
    });
    component.images = [];
    component.onSubmit();
    expect(component.formErrors).toEqual(
      'MARKETPLACE-PRODUCT-SELL-PAGE.PLEASE_UPLOAD_AT_LEAST_ONE_IMAGE_FOR_THE_PRODUCT',
    );
  });
});
