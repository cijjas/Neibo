import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
  waitForAsync,
} from '@angular/core/testing';
import { RejectedPageComponent } from './rejected-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';

// Import service types (adjust paths as needed)
import { NeighborhoodService, Neighborhood, Role } from '@shared/index';
import {
  AuthService,
  ToastService,
  UserSessionService,
  HateoasLinksService,
} from '@core/index';
import { UserService } from '@shared/index';
import { TranslateService } from '@ngx-translate/core';

// --- Fake Translate Pipe ---
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

describe('RejectedPageComponent', () => {
  let component: RejectedPageComponent;
  let fixture: ComponentFixture<RejectedPageComponent>;

  // Dummy data for a neighborhood.
  const dummyNeighborhood: Neighborhood = { self: 'n1', name: 'Neighborhood1' };

  // Create spies for services.
  const neighborhoodServiceSpy = jasmine.createSpyObj('NeighborhoodService', [
    'getNeighborhoods',
  ]);
  // (Not used directly in onSubmit; used in ngOnInit for loading combos.)
  neighborhoodServiceSpy.getNeighborhoods.and.returnValue(
    of({ neighborhoods: [dummyNeighborhood], currentPage: 1, totalPages: 1 }),
  );

  const userServiceSpy = jasmine.createSpyObj('UserService', [
    'requestNeighborhood',
  ]);
  const authServiceSpy = jasmine.createSpyObj('AuthService', ['logout']);
  const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
  const userSessionServiceSpy = jasmine.createSpyObj('UserSessionService', [
    'updateUserProperty',
    'setUserRole',
  ]);
  // HateoasLinksService is not used in onSubmit here.
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  // Provide a simple fake TranslateService.
  const fakeTranslateService = { instant: (key: string) => key };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [RejectedPageComponent, FakeTranslatePipe],
      providers: [
        { provide: NeighborhoodService, useValue: neighborhoodServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: UserSessionService, useValue: userSessionServiceSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: TranslateService, useValue: fakeTranslateService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(RejectedPageComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RejectedPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should handle valid neighborhood signup submission', fakeAsync(() => {
    // Set the form with a valid neighborhood.
    component.neighborhoodForm.setValue({ neighborhood: dummyNeighborhood });

    // Stub userService.requestNeighborhood to return success.
    userServiceSpy.requestNeighborhood.and.returnValue(of({}));

    // Call onSubmit.
    component.onSubmit();
    tick();

    // The form should be valid.
    expect(component.neighborhoodForm.valid).toBeTrue();
    // Verify that requestNeighborhood is called with dummyNeighborhood.self.
    expect(userServiceSpy.requestNeighborhood).toHaveBeenCalledWith(
      dummyNeighborhood.self,
    );
    // Verify that userSessionService.updateUserProperty is called with the proper values.
    expect(userSessionServiceSpy.updateUserProperty).toHaveBeenCalledWith(
      'userRole',
      Role.UNVERIFIED_NEIGHBOR,
    );
    expect(userSessionServiceSpy.updateUserProperty).toHaveBeenCalledWith(
      'userRoleEnum',
      Role.UNVERIFIED_NEIGHBOR,
    );
    expect(userSessionServiceSpy.updateUserProperty).toHaveBeenCalledWith(
      'userRoleDisplay',
      'Unverified',
    );
    expect(userSessionServiceSpy.setUserRole).toHaveBeenCalledWith(
      Role.UNVERIFIED_NEIGHBOR,
    );
    // Verify that the success toast is shown with the correct key.
    expect(toastServiceSpy.showToast).toHaveBeenCalledWith(
      'REJECTED-PAGE.SUCCESSFULLY_REQUESTED',
      'success',
    );
    // Verify that router.navigate is called with ['/unverified'].
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/unverified']);
    // And the submitted flag is set.
    expect(component.submitted).toBeTrue();
  }));

  it('should log out and navigate when goBackToMainPage is called', () => {
    component.goBackToMainPage();
    expect(authServiceSpy.logout).toHaveBeenCalled();
    // If the component also navigates in goBackToMainPage, you can test that here.
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });
});
