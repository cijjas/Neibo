import {
  ComponentFixture,
  TestBed,
  waitForAsync,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ServiceProvidersJoinNeighborhoodsPageComponent } from './service-providers-join-neighborhoods-page.component';
import { NeighborhoodService, AffiliationService } from '@shared/index';
import { HateoasLinksService, ToastService } from '@core/index';
import { TranslateService } from '@ngx-translate/core';
import { Affiliation, Neighborhood, LinkKey } from '@shared/models';

// ----- Fake Translate Pipe -----
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

// ----- Service Spies / Stubs -----
const affiliationServiceSpy = jasmine.createSpyObj('AffiliationService', [
  'getAffiliations',
  'deleteAffiliation',
  'createAffiliations',
]);
const neighborhoodServiceSpy = jasmine.createSpyObj('NeighborhoodService', [
  'getNeighborhoods',
]);
const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', ['getLink']);
const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);
const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

// Dummy data for affiliations and neighborhoods
const dummyAffiliations: Affiliation[] = [
  {
    neighborhood: { self: 'n1', name: 'Neighborhood 1' } as Neighborhood,
    worker: {} as any,
    role: 'member',
    self: 'aff1',
  },
  {
    neighborhood: { self: 'n2', name: 'Neighborhood 2' } as Neighborhood,
    worker: {} as any,
    role: 'member',
    self: 'aff2',
  },
];

const dummyNeighborhoods: Neighborhood[] = [
  { self: 'n3', name: 'Neighborhood 3' },
  { self: 'n4', name: 'Neighborhood 4' },
];

// Stub methods with all required properties.
affiliationServiceSpy.getAffiliations.and.returnValue(
  of({ affiliations: dummyAffiliations, totalPages: 3, currentPage: 2 }),
);
neighborhoodServiceSpy.getNeighborhoods.and.returnValue(
  of({ neighborhoods: dummyNeighborhoods, totalPages: 5, currentPage: 1 }),
);

// For our test, we force getLink to always return 'worker_self_link'
linkServiceSpy.getLink.and.returnValue('worker_self_link');

// Stub ActivatedRoute: we use simple queryParams and cast to ActivatedRoute.
const fakeActivatedRoute = {
  queryParams: of({ page: '2', size: '5' }),
} as unknown as ActivatedRoute;

describe('ServiceProvidersJoinNeighborhoodsPageComponent', () => {
  let component: ServiceProvidersJoinNeighborhoodsPageComponent;
  let fixture: ComponentFixture<ServiceProvidersJoinNeighborhoodsPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ServiceProvidersJoinNeighborhoodsPageComponent,
        FakeTranslatePipe,
      ],
      providers: [
        { provide: NeighborhoodService, useValue: neighborhoodServiceSpy },
        { provide: AffiliationService, useValue: affiliationServiceSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: TranslateService, useValue: { instant: (k: string) => k } },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(
      ServiceProvidersJoinNeighborhoodsPageComponent,
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Test: Initialization
  it('should initialize component with query params and load neighborhoods', () => {
    expect(component.currentAssociatedPage).toEqual(2);
    expect(component.pageSize).toEqual(5);

    // Verify getAffiliations was called with forWorker set to worker_self_link.
    expect(affiliationServiceSpy.getAffiliations).toHaveBeenCalledWith({
      forWorker: 'worker_self_link',
      page: 2,
      size: 5,
    });
    expect(component.associatedNeighborhoods).toEqual(dummyAffiliations);
    expect(neighborhoodServiceSpy.getNeighborhoods).toHaveBeenCalled();
    expect(component.otherNeighborhoods).toEqual(dummyNeighborhoods);
  });
});
