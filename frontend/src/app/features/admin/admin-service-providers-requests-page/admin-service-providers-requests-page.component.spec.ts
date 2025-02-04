import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminServiceProvidersRequestsPageComponent } from './admin-service-providers-requests-page.component';
import { WorkerService, AffiliationService, LinkKey } from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { ToastService } from '@core/index';
import { ConfirmationService } from '@core/index';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

// Create a fake translate pipe so the component template works.
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string, ...args: any[]): string {
    return value;
  }
}

describe('AdminServiceProvidersRequestsPageComponent', () => {
  let component: AdminServiceProvidersRequestsPageComponent;
  let fixture: ComponentFixture<AdminServiceProvidersRequestsPageComponent>;

  // Update the link service spy to return a neighborhood link as well.
  const mockLinkService = {
    getLink: jasmine.createSpy('getLink').and.callFake((key: string) => {
      if (
        key === LinkKey.VERIFIED_WORKER_ROLE ||
        key === 'root:verifiedWorkerRole' ||
        key === 'VERIFIED_WORKER_ROLE'
      ) {
        return 'verified_worker_link';
      } else if (
        key === LinkKey.UNVERIFIED_WORKER_ROLE ||
        key === 'root:unverifiedWorkerRole' ||
        key === 'UNVERIFIED_WORKER_ROLE'
      ) {
        return 'unverified_worker_link';
      } else if (
        key === LinkKey.NEIGHBORHOOD_SELF ||
        key === 'root:neighborhoodSelf' ||
        key === 'NEIGHBORHOOD_SELF'
      ) {
        return 'neighborhood_link';
      }
      return '';
    }),
  };

  const mockWorkerService = {
    getWorkers: jasmine
      .createSpy('getWorkers')
      .and.returnValue(
        of({ workers: [{ user: { name: 'Test Worker' } }], totalPages: 2 }),
      ),
  };

  const mockAffiliationService = {
    verifyWorker: jasmine.createSpy('verifyWorker').and.returnValue(of({})),
    rejectWorker: jasmine.createSpy('rejectWorker').and.returnValue(of({})),
  };

  const mockToastService = {
    showToast: jasmine.createSpy('showToast'),
  };

  const mockConfirmationService = {
    askForConfirmation: jasmine
      .createSpy('askForConfirmation')
      .and.returnValue(of(true)),
  };

  const mockTranslateService = {
    instant: jasmine
      .createSpy('instant')
      .and.callFake((key: string, params?: any) => key),
  };

  // Configure ActivatedRoute so that the component behaves as if it's on the "service-providers" route.
  const mockActivatedRoute = {
    url: of([{ path: 'service-providers' }]),
    queryParams: of({ page: 1, size: 10 }),
    snapshot: {
      url: [{ path: 'service-providers' }],
    },
    params: of({}),
  };

  const mockRouter = {
    navigate: jasmine.createSpy('navigate'),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        AdminServiceProvidersRequestsPageComponent,
        FakeTranslatePipe,
      ],
      providers: [
        { provide: WorkerService, useValue: mockWorkerService },
        { provide: AffiliationService, useValue: mockAffiliationService },
        { provide: HateoasLinksService, useValue: mockLinkService },
        { provide: ToastService, useValue: mockToastService },
        { provide: ConfirmationService, useValue: mockConfirmationService },
        { provide: TranslateService, useValue: mockTranslateService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: Router, useValue: mockRouter },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(
      AdminServiceProvidersRequestsPageComponent,
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load workers on init for service-providers route', () => {
    // In the "service-providers" branch, this.serviceProviders should be true.
    expect(mockLinkService.getLink).toHaveBeenCalledWith(
      'root:verifiedWorkerRole',
    );

    // Check the parameters passed to getWorkers.
    const lastCallArgs =
      mockWorkerService.getWorkers.calls.mostRecent().args[0];
    expect(lastCallArgs).toEqual({
      inNeighborhood: ['neighborhood_link'],
      withRole: 'verified_worker_link',
      page: 1,
      size: 10,
    });
    expect(component.workers.length).toBe(1);
    expect(component.workers[0].user.name).toBe('Test Worker');
    expect(component.totalPages).toBe(2);
    expect(component.serviceProviders).toBeTrue();
  });

  it('should verify worker successfully', () => {
    // Reset the getWorkers spy so we only count calls triggered by verifyWorker.
    mockWorkerService.getWorkers.calls.reset();
    const testWorker = {
      self: 'worker1',
      user: { name: 'WorkerToVerify' },
    } as any;

    component.verifyWorker(testWorker);

    expect(mockAffiliationService.verifyWorker).toHaveBeenCalledWith(
      testWorker.self,
    );
    expect(mockToastService.showToast).toHaveBeenCalledWith(
      'ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.WORKER_WAS_VERIFIED_SUCCESSFULLY',
      'success',
    );
    // The verifyWorker success callback calls loadWorkers.
    expect(mockWorkerService.getWorkers).toHaveBeenCalled();
  });

  it('should reject worker after confirmation', () => {
    // Reset the getWorkers spy so we only count calls triggered by rejectWorker.
    mockWorkerService.getWorkers.calls.reset();
    const testWorker = {
      self: 'worker2',
      user: { name: 'WorkerToReject' },
    } as any;

    component.rejectWorker(testWorker);

    expect(mockConfirmationService.askForConfirmation).toHaveBeenCalled();
    expect(mockAffiliationService.rejectWorker).toHaveBeenCalledWith(
      testWorker.self,
    );

    // For the "service-providers" branch, the success toast message is for "removed as a service provider".
    expect(mockToastService.showToast).toHaveBeenCalledWith(
      'ADMIN-SERVICE-PROVIDERS-REQUESTS-PAGE.WORKERUSERNAME_HAS_BEEN_SUCCESSFULLY_REMOVED_AS_A_',
      'success',
    );
    // After rejection, loadWorkers should be called.
    expect(mockWorkerService.getWorkers).toHaveBeenCalled();
  });
});
