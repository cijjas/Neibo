import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';

import { ServiceProvidersPreviewComponent } from './service-providers-preview.component';
import { environment } from '../../../../environments/environment';
import { Worker, User, Profession } from '@shared/index';

describe('ServiceProvidersPreviewComponent', () => {
  let component: ServiceProvidersPreviewComponent;
  let fixture: ComponentFixture<ServiceProvidersPreviewComponent>;
  let routerSpy: jasmine.SpyObj<Router>;
  const activatedRouteStub = {} as ActivatedRoute;

  beforeEach(waitForAsync(() => {
    (environment as any).deployUrl = 'http://test.com/';

    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      declarations: [ServiceProvidersPreviewComponent],
      imports: [RouterTestingModule],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA], 
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceProvidersPreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should set profileImageUrl and backgroundImageUrl based on worker input', () => {
    const dummyUser: User = {
      email: 'user@test.com',
      name: 'User',
      surname: 'Test',
      darkMode: false,
      phoneNumber: '1234567890',
      identification: 1,
      creationDate: new Date(),
      language: 'en',
      userRole: 'worker',
      userRoleEnum: null,
      userRoleDisplay: 'Worker',
      image: 'profile.jpg',
      self: 'user1',
    };

    const dummyWorkerWithImages: Worker = {
      phoneNumber: '111-222-3333',
      businessName: 'Business',
      address: 'Address',
      bio: 'Bio',
      averageRating: 4.5,
      user: dummyUser,
      reviews: 'reviewsUrl',
      totalReviews: 10,
      totalPosts: 5,
      posts: 'postsUrl',
      backgroundImage: 'bg.jpg',
      neighborhoodAffiliated: [],
      professions: [],
      self: 'worker1',
    };

    component.worker = dummyWorkerWithImages;
    component.ngOnInit();
    expect(component.profileImageUrl).toEqual('profile.jpg');
    expect(component.backgroundImageUrl).toEqual('bg.jpg');

    const dummyWorkerNoImages: Worker = {
      ...dummyWorkerWithImages,
      user: { ...dummyUser, image: '' },
      backgroundImage: '',
    };
    component.worker = dummyWorkerNoImages;
    component.ngOnInit();
    expect(component.profileImageUrl).toEqual(
      'http://test.com/assets/images/default-profile.png',
    );
    expect(component.backgroundImageUrl).toEqual(
      'http://test.com/assets/images/default-background.png',
    );
  });

  it('should navigate with proper query params when setProfession is called', () => {
    const dummyProfession: Profession = {
      name: 'Plumber',
      displayName: 'Plumber',
      self: 'prof1',
    };

    component.setProfession(dummyProfession);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/services'], {
      relativeTo: activatedRouteStub,
      queryParams: { withProfession: 'prof1' },
    });

    routerSpy.navigate.calls.reset();

    component.setProfession(null);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/services'], {
      relativeTo: activatedRouteStub,
      queryParams: { withProfession: null },
    });
  });

  it('should call setProfession and stop propagation when onProfessionClick is triggered', () => {
    const dummyProfession: Profession = {
      name: 'Electrician',
      displayName: 'Electrician',
      self: 'prof2',
    };

    const fakeEvent = new MouseEvent('click');
    spyOn(fakeEvent, 'stopPropagation');

    spyOn(component, 'setProfession');

    component.onProfessionClick(fakeEvent, dummyProfession);
    expect(fakeEvent.stopPropagation).toHaveBeenCalled();
    expect(component.setProfession).toHaveBeenCalledWith(dummyProfession);
  });
});
