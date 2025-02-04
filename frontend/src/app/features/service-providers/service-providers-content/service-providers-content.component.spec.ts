import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  Router,
  ActivatedRoute,
  ActivatedRouteSnapshot,
  Params,
  Data,
  UrlSegment,
  convertToParamMap,
} from '@angular/router';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

import { ServiceProvidersContentComponent } from './service-providers-content.component';
import { environment } from 'environments/environment';

// Dummy environment override for testing.
const fakeEnvironment = { deployUrl: 'http://test.com/' };

// Dummy data for User, Profession, and Worker.
const dummyUser = {
  email: 'user@test.com',
  name: 'John',
  surname: 'Doe',
  darkMode: false,
  phoneNumber: '1234567890',
  identification: 1,
  creationDate: new Date(),
  language: 'en',
  userRole: 'worker',
  userRoleEnum: null,
  userRoleDisplay: 'Worker',
  image: 'worker-profile.jpg',
  self: 'user1',
};

const dummyProfession = {
  name: 'Plumber',
  displayName: 'Plumber',
  self: 'prof1',
};

const dummyWorker = {
  phoneNumber: '111-222-3333',
  businessName: 'Best Plumbing',
  address: '123 Main St',
  bio: 'Experienced plumber',
  averageRating: 4.5,
  user: dummyUser,
  reviews: 'review-url',
  totalReviews: 10,
  totalPosts: 5,
  posts: 'posts-url',
  backgroundImage: 'worker-bg.jpg',
  neighborhoodAffiliated: ['Neighborhood 1'],
  professions: [dummyProfession],
  self: 'worker1',
};

//
// Fake Translate Pipe (if your template uses it)
//
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

//
// Create a complete dummy ActivatedRouteSnapshot:
//
const dummyActivatedRouteSnapshot: ActivatedRouteSnapshot = {
  url: [] as UrlSegment[],
  params: {} as Params,
  queryParams: {} as Params,
  fragment: '',
  data: { product: dummyWorker } as Data,
  outlet: 'primary',
  component: null,
  routeConfig: null,
  root: {} as ActivatedRouteSnapshot,
  parent: null,
  firstChild: null,
  children: [],
  paramMap: convertToParamMap({}),
  queryParamMap: convertToParamMap({}),
  toString: () => '',
  title: '',
  pathFromRoot: [],
};

//
// Create a full dummy ActivatedRoute using the snapshot:
//
const activatedRouteStub: ActivatedRoute = {
  snapshot: dummyActivatedRouteSnapshot,
  url: of([]),
  params: of({}),
  queryParams: of({}),
  fragment: of(''),
  data: of({ product: dummyWorker }),
  outlet: 'primary',
  component: null,
  routeConfig: null,
  root: {} as ActivatedRoute,
  parent: null,
  firstChild: null,
  children: [],
  pathFromRoot: [],
  toString: () => '',
  title: undefined,
  paramMap: undefined,
  queryParamMap: undefined,
};

describe('ServiceProvidersContentComponent', () => {
  let component: ServiceProvidersContentComponent;
  let fixture: ComponentFixture<ServiceProvidersContentComponent>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      declarations: [ServiceProvidersContentComponent, FakeTranslatePipe],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
      schemas: [NO_ERRORS_SCHEMA], // Ignore unknown elements/pipes.
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceProvidersContentComponent);
    component = fixture.componentInstance;
    (environment as any).deployUrl = fakeEnvironment.deployUrl;
    component.worker = dummyWorker;
    fixture.detectChanges();
  });

  // Test 1: Getter behavior for background and profile image URLs.
  it('should return correct background and profile image URLs', () => {
    // When worker has valid images.
    expect(component.backgroundImageUrl).toEqual(dummyWorker.backgroundImage);
    expect(component.profileImageUrl).toEqual(dummyWorker.user.image);

    // Test fallback when images are missing.
    component.worker = {
      ...dummyWorker,
      backgroundImage: '',
      user: { ...dummyWorker.user, image: '' },
    };
    expect(component.backgroundImageUrl).toEqual(
      fakeEnvironment.deployUrl + 'assets/images/default-background.png',
    );
    expect(component.profileImageUrl).toEqual(
      fakeEnvironment.deployUrl + 'assets/images/default-profile.png',
    );
  });

  // Test 2: setProfession() navigates correctly.
  it('should navigate with proper query params when setting profession', () => {
    component.setProfession(dummyProfession);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/services'], {
      relativeTo: activatedRouteStub,
      queryParams: { withProfession: dummyProfession.self },
    });

    routerSpy.navigate.calls.reset();
    component.setProfession(null);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/services'], {
      relativeTo: activatedRouteStub,
      queryParams: { withProfession: null },
    });
  });

  // Test 3: Verify that openEditProfile output event is emitted.
  it('should emit openEditProfile event', () => {
    spyOn(component.openEditProfile, 'emit');
    component.openEditProfile.emit();
    expect(component.openEditProfile.emit).toHaveBeenCalled();
  });
});
