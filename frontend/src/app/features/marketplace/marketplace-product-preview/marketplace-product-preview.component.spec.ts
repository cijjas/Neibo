import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MarketplaceProductPreviewComponent } from './marketplace-product-preview.component';
import { Router } from '@angular/router';
import { Product, Department } from '@shared/index';
import { environment } from 'environments/environment';

describe('MarketplaceProductPreviewComponent', () => {
  let component: MarketplaceProductPreviewComponent;
  let fixture: ComponentFixture<MarketplaceProductPreviewComponent>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [MarketplaceProductPreviewComponent],
      providers: [{ provide: Router, useValue: routerSpy }],
    })
      .overrideTemplate(MarketplaceProductPreviewComponent, `<div></div>`)
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketplaceProductPreviewComponent);
    component = fixture.componentInstance;
    // Provide a dummy product with the required properties
    component.product = {
      self: 'product1',
      firstImage: 'http://example.com/product1.png',
      name: 'Test Product',
      description: 'A test product',
      price: 10,
      used: false,
      stock: 5,
      inquiries: '',
      totalPendingRequests: 0,
      createdAt: new Date(),
      secondImage: '',
      thirdImage: '',
      seller: {} as any, // Dummy
      department: {} as any, // Dummy
    } as Product;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to product detail when onProductClick is called', () => {
    component.onProductClick();
    expect(routerSpy.navigate).toHaveBeenCalledWith([
      '/marketplace/products',
      component.product.self,
    ]);
  });

  it('should return the product image if available', () => {
    const image = component.getProductImage();
    expect(image).toEqual('http://example.com/product1.png');
  });

  it('should return the default product image if firstImage is empty', () => {
    // Set the product image to an empty string
    component.product.firstImage = '';
    const image = component.getProductImage();
    expect(image).toEqual(
      environment.deployUrl + 'assets/images/default-product.png',
    );
  });

  it('should navigate to marketplace with department query param when goToDepartment is called', () => {
    const fakeDepartment: Department = {
      self: 'department1',
      name: 'Department 1',
      displayName: 'Department 1',
    };
    component.goToDepartment(fakeDepartment);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/marketplace'], {
      queryParams: { inDepartment: fakeDepartment.self },
    });
  });
});
