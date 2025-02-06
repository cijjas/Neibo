import {
  ComponentFixture,
  TestBed,
  waitForAsync,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ServiceProvidersPageComponent } from './service-providers-page.component';
import { Profession, User, Worker, WorkerService } from '@shared/index';
import { TranslateService } from '@ngx-translate/core';

// Create a complete dummy User object.
const dummyUser: User = {
  email: 'test@example.com',
  name: 'Test',
  surname: 'User',
  darkMode: false,
  phoneNumber: '1234567890',
  identification: 1,
  creationDate: new Date(),
  language: 'en',
  userRole: 'worker',
  userRoleEnum: null, // Replace with an appropriate enum value if needed.
  userRoleDisplay: 'Worker',
  image: 'profile.jpg',
  self: 'user1',
};

// Optionally, define a dummy Profession array (if needed).
const dummyProfessions: Profession[] = []; // or provide actual dummy professions

// Create a complete dummy Worker object.
const dummyWorker: Worker = {
  phoneNumber: '111-222-3333',
  businessName: 'Test Business',
  address: '123 Main St',
  bio: 'This is a test bio.',
  averageRating: 4.5,
  user: dummyUser,
  reviews: 'reviewsUrl',
  totalReviews: 10,
  totalPosts: 5,
  posts: 'postsUrl',
  backgroundImage: 'bg.jpg',
  neighborhoodAffiliated: ['Neighborhood1'],
  professions: dummyProfessions,
  self: 'worker1',
};

describe('ServiceProvidersPageComponent', () => {
  let component: ServiceProvidersPageComponent;
  let fixture: ComponentFixture<ServiceProvidersPageComponent>;
  let workerServiceSpy: jasmine.SpyObj<WorkerService>;
  let routerSpy: jasmine.SpyObj<Router>;

  // Create an array of complete dummy Workers.
  const dummyWorkers: Worker[] = [
    dummyWorker,
    {
      ...dummyWorker,
      self: 'worker2',
      businessName: 'Another Business',
      backgroundImage: 'bg2.jpg',
    },
  ];

  beforeEach(waitForAsync(() => {
    // Create a spy for WorkerService with a stub for getWorkers.
    workerServiceSpy = jasmine.createSpyObj('WorkerService', ['getWorkers']);
    // Return an observable with dummy data that includes totalPages and currentPage.
    workerServiceSpy.getWorkers.and.returnValue(
      of({ workers: dummyWorkers, totalPages: 5, currentPage: 2 }),
    );

    // Create a simple ActivatedRoute stub with queryParams.
    const activatedRouteStub = {
      queryParams: of({ page: '2', size: '15', withProfession: 'plumber' }),
    } as unknown as ActivatedRoute;

    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      declarations: [ServiceProvidersPageComponent],
      providers: [
        { provide: WorkerService, useValue: workerServiceSpy },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: Router, useValue: routerSpy },
        {
          provide: TranslateService,
          useValue: { instant: (key: string) => key },
        },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceProvidersPageComponent);
    component = fixture.componentInstance;
    // Trigger ngOnInit() and subscribe to queryParams.
    fixture.detectChanges();
  });

  it('should initialize with query params and load workers', fakeAsync(() => {
    // After initialization, we expect:
    // - currentPage is 2 (converted from '2')
    // - pageSize is 15 (converted from '15')
    // - professions is set to an array ['plumber']
    expect(component.currentPage).toEqual(2);
    expect(component.pageSize).toEqual(15);
    expect(component.professions).toEqual(['plumber']);

    // Allow asynchronous loadWorkers() to complete.
    tick();

    // Expect getWorkers to be called with the proper query parameters.
    expect(workerServiceSpy.getWorkers).toHaveBeenCalledWith({
      page: 2,
      size: 15,
      withProfession: ['plumber'],
    });

    // Expect the component's workersList and totalPages to be updated from the response.
    expect(component.workersList).toEqual(dummyWorkers);
    expect(component.totalPages).toEqual(5);

    // isLoading should be false after successful loading.
    expect(component.isLoading).toBeFalse();
  }));
});
