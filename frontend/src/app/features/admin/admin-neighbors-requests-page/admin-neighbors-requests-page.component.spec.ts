import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminNeighborsRequestsPageComponent } from './admin-neighbors-requests-page.component';
import { UserService } from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { ToastService } from '@core/index';
import { ConfirmationService } from '@core/index';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

// Create a fake translate pipe so the component template works
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string, ...args: any[]): string {
    return value;
  }
}

describe('AdminNeighborsRequestsPageComponent', () => {
  let component: AdminNeighborsRequestsPageComponent;
  let fixture: ComponentFixture<AdminNeighborsRequestsPageComponent>;

  const mockLinkService = {
    getLink: jasmine.createSpy('getLink').and.callFake((key: string) => {
      if (key === 'root:neighborUserRole' || key === 'NEIGHBOR_USER_ROLE') {
        return 'neighbor_link';
      } else if (
        key === 'root:unverifiedNeighborUserRole' ||
        key === 'UNVERIFIED_NEIGHBOR_USER_ROLE'
      ) {
        return 'unverified_neighbor_link';
      }
      return '';
    }),
  };

  const mockUserService = {
    getUsers: jasmine
      .createSpy('getUsers')
      .and.returnValue(of({ users: [{ name: 'Test User' }], totalPages: 2 })),
    verifyUser: jasmine.createSpy('verifyUser').and.returnValue(of({})),
    rejectUser: jasmine.createSpy('rejectUser').and.returnValue(of({})),
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

  // Configure the ActivatedRoute so that the component behaves as if it's on the "neighbors" route.
  const mockActivatedRoute = {
    url: of([{ path: 'neighbors' }]),
    queryParams: of({ page: 1, size: 10 }),
    snapshot: {
      url: [{ path: 'neighbors' }],
    },
    params: of({}),
  };

  const mockRouter = {
    navigate: jasmine.createSpy('navigate'),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminNeighborsRequestsPageComponent, FakeTranslatePipe],
      providers: [
        { provide: UserService, useValue: mockUserService },
        { provide: HateoasLinksService, useValue: mockLinkService },
        { provide: ToastService, useValue: mockToastService },
        { provide: ConfirmationService, useValue: mockConfirmationService },
        { provide: TranslateService, useValue: mockTranslateService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: Router, useValue: mockRouter },
      ],
      // We still use NO_ERRORS_SCHEMA so that unknown elements in the template are ignored.
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminNeighborsRequestsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load users on init for neighbors route', () => {
    // In the "neighbors" route branch, the component sets this.neighbors = true
    // and calls getLink with LinkKey.NEIGHBOR_USER_ROLE (which is 'root:neighborUserRole' here).
    expect(mockLinkService.getLink).toHaveBeenCalledWith(
      'root:neighborUserRole',
    );

    // Check the most recent call to getUsers. We expect userRole to be 'neighbor_link'.
    const lastCallArgs = mockUserService.getUsers.calls.mostRecent().args[0];
    expect(lastCallArgs).toEqual({
      userRole: 'neighbor_link',
      page: 1,
      size: 10,
    });
    expect(component.users.length).toBe(1);
    expect(component.users[0].name).toBe('Test User');
    expect(component.totalPages).toBe(2);
    expect(component.neighbors).toBeTrue();
  });

  it('should verify user successfully', () => {
    // Reset the getUsers spy so we only count calls triggered by verifyUser.
    mockUserService.getUsers.calls.reset();
    const testUser = { name: 'UserToVerify' } as any;

    component.verifyUser(testUser);

    expect(mockUserService.verifyUser).toHaveBeenCalledWith(testUser);
    expect(mockToastService.showToast).toHaveBeenCalledWith(
      'ADMIN-NEIGHBORS-REQUESTS-PAGE.USER_USERNAME_HAS_BEEN_SUCCESSFULLY_VERIFIED_AS_A_',
      'success',
    );
    // The verifyUser success callback calls loadUsers, so getUsers should be called at least once.
    expect(mockUserService.getUsers).toHaveBeenCalled();
  });

  it('should reject user after confirmation', () => {
    // Reset the getUsers spy so we only count calls triggered by rejectUser.
    mockUserService.getUsers.calls.reset();
    const testUser = { name: 'UserToReject' } as any;

    component.rejectUser(testUser);

    expect(mockConfirmationService.askForConfirmation).toHaveBeenCalled();
    expect(mockUserService.rejectUser).toHaveBeenCalledWith(testUser);

    // For the "neighbors" branch, the success toast message is for "removed as a neighbor".
    expect(mockToastService.showToast).toHaveBeenCalledWith(
      'ADMIN-NEIGHBORS-REQUESTS-PAGE.USERNAME_HAS_BEEN_SUCCESSFULLY_REMOVED_AS_A_NEIGHB',
      'success',
    );
    // After rejection, the component calls loadUsers.
    expect(mockUserService.getUsers).toHaveBeenCalled();
  });
});
