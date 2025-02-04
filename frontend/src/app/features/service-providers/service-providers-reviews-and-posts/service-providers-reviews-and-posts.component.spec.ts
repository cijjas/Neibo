import {
  ComponentFixture,
  TestBed,
  waitForAsync,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, Subject } from 'rxjs';
import { CUSTOM_ELEMENTS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

import { ServiceProvidersReviewsAndPostsComponent } from './service-providers-reviews-and-posts.component';
import {
  ReviewService,
  PostService,
  WorkerService,
  Review,
  Post,
  Worker,
  LinkKey,
} from '@shared/index';
import { HateoasLinksService } from '@core/index';

/**
 * A fake pipe that simply returns its input. This satisfies any usage of | translate
 * in the component template, eliminating the “NG0302: The pipe 'translate' could not be found” error.
 */
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

describe('ServiceProvidersReviewsAndPostsComponent', () => {
  let component: ServiceProvidersReviewsAndPostsComponent;
  let fixture: ComponentFixture<ServiceProvidersReviewsAndPostsComponent>;

  // Service spies
  let reviewServiceSpy: jasmine.SpyObj<ReviewService>;
  let postServiceSpy: jasmine.SpyObj<PostService>;
  let workerServiceSpy: jasmine.SpyObj<WorkerService>;
  let linkServiceSpy: jasmine.SpyObj<HateoasLinksService>;
  let routerSpy: jasmine.SpyObj<Router>;

  // Dummy data for reviews and posts
  const dummyReviews: Review[] = [
    {
      rating: 5,
      message: 'Great service!',
      createdAt: new Date().toISOString(),
      self: 'rev1',
    },
  ] as any;
  const dummyPosts: Post[] = [
    {
      title: 'Post Title',
      body: 'Post Body',
      createdAt: new Date().toISOString(),
      self: 'pst1',
    },
  ] as any;

  // Minimal dummy worker
  const dummyWorker: Worker = {
    phoneNumber: '111-222-3333',
    businessName: 'Business',
    address: 'Address',
    bio: 'Bio',
    averageRating: 4.5,
    user: {} as any,
    reviews: 'reviewsUrl',
    totalReviews: 10,
    totalPosts: 5,
    posts: 'postsUrl',
    backgroundImage: 'bg.jpg',
    neighborhoodAffiliated: [],
    professions: [],
    self: 'worker123',
  };

  // Create a Subject to simulate queryParams changes
  let fakeQueryParams$ = new Subject<any>();

  // Provide a minimal ActivatedRoute stub with snapshot.paramMap and queryParams
  const activatedRouteStub = {
    snapshot: {
      paramMap: {
        get: (key: string) => (key === 'id' ? 'worker123' : null),
      },
    },
    queryParams: fakeQueryParams$.asObservable(),
  } as unknown as ActivatedRoute;

  beforeEach(waitForAsync(() => {
    reviewServiceSpy = jasmine.createSpyObj('ReviewService', ['getReviews']);
    postServiceSpy = jasmine.createSpyObj('PostService', ['getWorkerPosts']);
    workerServiceSpy = jasmine.createSpyObj('WorkerService', ['getWorker']);
    linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', ['getLink']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    // Stub getReviews to return dummy reviews with currentPage
    reviewServiceSpy.getReviews.and.returnValue(
      of({ reviews: dummyReviews, totalPages: 2, currentPage: 1 }),
    );
    // Stub getWorkerPosts to return dummy posts with currentPage
    postServiceSpy.getWorkerPosts.and.returnValue(
      of({ posts: dummyPosts, totalPages: 3, currentPage: 1 }),
    );
    // Stub getWorker to return dummyWorker
    workerServiceSpy.getWorker.and.returnValue(of(dummyWorker));

    // Stub linkService.getLink to return values that make isTheWorker/isWorker = true
    linkServiceSpy.getLink.and.callFake((key: string) => {
      switch (key) {
        case LinkKey.USER_WORKER:
          return 'worker123'; // matches dummyWorker.self => isTheWorker = true
        case LinkKey.USER_USER_ROLE:
          return 'worker'; // some role
        case LinkKey.WORKER_USER_ROLE:
          return 'worker'; // so isWorker = true
        default:
          return '';
      }
    });

    TestBed.configureTestingModule({
      declarations: [
        ServiceProvidersReviewsAndPostsComponent,
        FakeTranslatePipe, // Declare the fake translate pipe
      ],
      providers: [
        { provide: ReviewService, useValue: reviewServiceSpy },
        { provide: PostService, useValue: postServiceSpy },
        { provide: WorkerService, useValue: workerServiceSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(fakeAsync(() => {
    fixture = TestBed.createComponent(ServiceProvidersReviewsAndPostsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit => calls loadWorker('worker123')
    tick(); // flush async from loadWorker promise
  }));

  // Test 1: Initialization
  it('should load the worker, set isTheWorker/isWorker, and update pagination & tab from query params', fakeAsync(() => {
    expect(workerServiceSpy.getWorker).toHaveBeenCalledWith('worker123');
    expect(component.worker).toEqual(dummyWorker);

    // Because linkService matches 'worker123', isTheWorker should be true, isWorker also true
    expect(component.isTheWorker).toBeTrue();
    expect(component.isWorker).toBeTrue();

    // Now simulate query params => { reviewPage: '2', reviewSize: '5', postPage: '3', postSize: '7', tab: 'posts' }
    fakeQueryParams$.next({
      reviewPage: '2',
      reviewSize: '5',
      postPage: '3',
      postSize: '7',
      tab: 'posts',
    });
    tick(); // flush

    expect(component.reviewCurrentPage).toBe(2);
    expect(component.reviewPageSize).toBe(5);
    expect(component.postCurrentPage).toBe(3);
    expect(component.postPageSize).toBe(7);
    expect(component.selectedTab).toBe('posts');

    // Also expect the local arrays to reflect the stub responses:
    expect(component.reviews).toEqual(dummyReviews);
    expect(component.reviewTotalPages).toBe(2);
    expect(component.posts).toEqual(dummyPosts);
    expect(component.postTotalPages).toBe(3);
  }));

  // Test 2: loadWorker => sets worker
  it('should set worker correctly when loadWorker is called', fakeAsync(async () => {
    const altWorker = { ...dummyWorker, self: 'alt1' };
    workerServiceSpy.getWorker.and.returnValue(of(altWorker));

    await component.loadWorker('alt1');
    tick();

    expect(workerServiceSpy.getWorker).toHaveBeenCalledWith('alt1');
    expect(component.worker.self).toBe('alt1');
  }));

  // Test 3: onTabChange => updates tab query param
  it('should update tab param when onTabChange is called', () => {
    component.onTabChange('posts');
    expect(routerSpy.navigate).toHaveBeenCalledWith([], {
      relativeTo: activatedRouteStub,
      queryParams: { tab: 'posts' },
      queryParamsHandling: 'merge',
    });
    expect(component.selectedTab).toBe('posts');
  });

  // Test 4: onDestroy => unsubscribes
  it('should unsubscribe from all subscriptions on destroy', () => {
    spyOn(component['subscriptions'], 'unsubscribe');
    component.ngOnDestroy();
    expect(component['subscriptions'].unsubscribe).toHaveBeenCalled();
  });
});
