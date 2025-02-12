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
import {
  LinkKey,
  Profession,
  User,
  Worker,
  WorkerService,
} from '@shared/index';
import { TranslateService } from '@ngx-translate/core';
import { HateoasLinksService, UserSessionService } from '@core/index';

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
  userRoleEnum: null,
  userRoleDisplay: 'Worker',
  image: 'profile.jpg',
  self: 'user1',
};

const dummyProfessions: Profession[] = [];

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

const hateoasLinksServiceStub = {
  getLink: (key: string) => {
    if (key === LinkKey.VERIFIED_WORKER_ROLE) {
      return 'verified-worker-role-url';
    }
    return undefined;
  },
};
describe('ServiceProvidersPageComponent', () => {
  let component: ServiceProvidersPageComponent;
  let fixture: ComponentFixture<ServiceProvidersPageComponent>;
  let workerServiceSpy: jasmine.SpyObj<WorkerService>;
  let routerSpy: jasmine.SpyObj<Router>;

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
    workerServiceSpy = jasmine.createSpyObj('WorkerService', ['getWorkers']);
    workerServiceSpy.getWorkers.and.returnValue(
      of({ workers: dummyWorkers, totalPages: 5, currentPage: 2 }),
    );

    const activatedRouteStub = {
      queryParams: of({ page: '2', size: '15', withProfession: 'plumber' }),
    } as unknown as ActivatedRoute;

    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    const userSessionServiceStub = {
      getCurrentUserValue: () => dummyUser,
    };

    const hateoasLinksServiceStub = {
      getLink: (key: string) => undefined,
    };

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
        { provide: UserSessionService, useValue: userSessionServiceStub },
        { provide: HateoasLinksService, useValue: hateoasLinksServiceStub },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceProvidersPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should initialize with query params and load workers', fakeAsync(() => {
    expect(component.currentPage).toEqual(2);
    expect(component.pageSize).toEqual(15);
    expect(component.professions).toEqual(['plumber']);

    tick();

    expect(workerServiceSpy.getWorkers).toHaveBeenCalledWith(
      jasmine.objectContaining({
        page: 2,
        size: 15,
        withProfession: ['plumber'],
      }),
    );

    expect(component.workersList).toEqual(dummyWorkers);
    expect(component.totalPages).toEqual(5);

    expect(component.isLoading).toBeFalse();
  }));
});
