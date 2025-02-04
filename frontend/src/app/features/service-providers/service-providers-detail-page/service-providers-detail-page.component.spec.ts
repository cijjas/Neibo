import {
  ComponentFixture,
  TestBed,
  waitForAsync,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

import { ServiceProvidersDetailPageComponent } from './service-providers-detail-page.component';
import {
  WorkerService,
  ReviewService,
  Worker,
  WorkerDto,
  LinkKey,
  User,
} from '@shared/index';
import { ToastService, HateoasLinksService } from '@core/index';
import { TranslateService } from '@ngx-translate/core';

// ----- Fake Translate Pipe -----
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

// ----- Dummy Data -----
// Minimal dummy user
const dummyUser: User = {
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
  image: 'profile.jpg',
  self: 'worker1',
};

// Minimal dummy worker
const dummyWorker: Worker = {
  phoneNumber: '111-222-3333',
  businessName: 'Awesome Services',
  address: '123 Main St',
  bio: 'Experienced service provider',
  averageRating: 4.7,
  user: dummyUser,
  reviews: 'reviewUrl',
  totalReviews: 5,
  totalPosts: 10,
  posts: 'postsUrl',
  backgroundImage: 'bg.jpg',
  neighborhoodAffiliated: ['Neighborhood 1'],
  professions: [],
  self: 'worker1',
};

// ----- Service Spies / Stubs -----
const workerServiceSpy = jasmine.createSpyObj('WorkerService', [
  'getWorker',
  'updateWorker',
]);
const reviewServiceSpy = jasmine.createSpyObj('ReviewService', [
  'createReview',
]);
const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);
const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', ['getLink']);
const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

// Stub TranslateService minimally.
const fakeTranslateService = {
  instant: (key: string) => key,
};

// ----- ActivatedRoute Stub -----
// We only need to supply route data (an observable with { worker: dummyWorker })
const activatedRouteStub = {
  data: of({ worker: dummyWorker }),
} as unknown as ActivatedRoute;

describe('ServiceProvidersDetailPageComponent', () => {
  let component: ServiceProvidersDetailPageComponent;
  let fixture: ComponentFixture<ServiceProvidersDetailPageComponent>;

  beforeEach(waitForAsync(() => {
    // For example, if linkService.getLink is used in isTheWorker() or onSubmitReview.
    linkServiceSpy.getLink.and.callFake((key: string) => {
      if (key === LinkKey.USER_SELF) {
        return 'user_self';
      }
      if (key === LinkKey.USER_WORKER) {
        return 'worker1';
      }
      return '';
    });

    TestBed.configureTestingModule({
      declarations: [ServiceProvidersDetailPageComponent, FakeTranslatePipe],
      providers: [
        { provide: WorkerService, useValue: workerServiceSpy },
        { provide: ReviewService, useValue: reviewServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: Router, useValue: routerSpy },
        { provide: TranslateService, useValue: fakeTranslateService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceProvidersDetailPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Test 1: Initialization – worker is set from route data.
  it('should initialize worker from route data', () => {
    expect(component.worker).toEqual(dummyWorker);
  });

  // Test 2: Dialog Toggle – openReviewDialog and closeReviewDialog toggle reviewDialogVisible.
  it('should toggle reviewDialogVisible when opening and closing review dialog', () => {
    component.openReviewDialog();
    expect(component.reviewDialogVisible).toBeTrue();

    component.closeReviewDialog();
    expect(component.reviewDialogVisible).toBeFalse();
  });

  // Test 3: onSubmitReview – calls reviewService.createReview, shows toast, and calls child reloadReviews if defined.
  it('should submit a review and show a success toast, then reload reviews', fakeAsync(() => {
    // Prepare a fake review.
    const fakeReview = { rating: 5, message: 'Great service!' };

    // Stub reviewService.createReview to return a success observable.
    reviewServiceSpy.createReview.and.returnValue(of({}));

    // Set up a fake child component with a reloadReviews spy.
    component.tabbedBox = {
      reloadReviews: jasmine.createSpy('reloadReviews'),
    } as any;

    // Call onSubmitReview.
    component.onSubmitReview(fakeReview);
    tick();

    // Expect that toastService.showToast was called with the success message.
    expect(toastServiceSpy.showToast).toHaveBeenCalledWith(
      'SERVICE-PROVIDERS-DETAIL-PAGE.REVIEW_SUBMITTED_SUCCESSFULLY',
      'success',
    );
    // Expect that the child's reloadReviews() was called.
    expect(component.tabbedBox.reloadReviews).toHaveBeenCalled();
    // Also, reviewDialogVisible should now be false.
    expect(component.reviewDialogVisible).toBeFalse();
  }));
});
