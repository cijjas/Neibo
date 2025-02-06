import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavigationEnd, Router } from '@angular/router';
import { of } from 'rxjs';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { ServiceProvidersLayoutComponent } from './service-providers-layout.component';

describe('ServiceProvidersLayoutComponent', () => {
  let component: ServiceProvidersLayoutComponent;
  let fixture: ComponentFixture<ServiceProvidersLayoutComponent>;
  let routerStub: Partial<Router>;

  beforeEach(async () => {
    routerStub = {
      url: '/services',
      events: of(new NavigationEnd(1, '/services', '/services')),
    };

    await TestBed.configureTestingModule({
      declarations: [ServiceProvidersLayoutComponent],
      providers: [{ provide: Router, useValue: routerStub }],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceProvidersLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should set isServicesRoute to true when router.url is "/services"', () => {
    expect(component.isServicesRoute).toBeTrue();
  });
});
