import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
  waitForAsync,
} from '@angular/core/testing';
import { SignupDialogComponent } from './signup-dialog.component';

import { ReactiveFormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { of } from 'rxjs';
import { Router } from '@angular/router';

// Import service types (adjust paths as needed)
import {
  NeighborhoodService,
  LanguageService,
  ProfessionService,
  UserService,
  WorkerService,
  Language,
  Profession,
  LinkKey,
  Role,
} from '@shared/index';
import { AuthService } from '@core/index';
import {
  HateoasLinksService,
  ToastService,
  UserSessionService,
} from '@core/index';
import { TranslateService } from '@ngx-translate/core';
import { encodeUrlSafeBase64 } from '@shared/utils/url-safe-base64.util';

// Dummy interfaces for Neighborhood if needed.
export interface Neighborhood {
  self: string;
  name: string;
}

// Fake translate pipe so that usage of "| translate" in the template does not cause errors.
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

describe('SignupDialogComponent - Submission', () => {
  let component: SignupDialogComponent;
  let fixture: ComponentFixture<SignupDialogComponent>;

  // Dummy responses for data fetching (used during ngOnInit)
  const dummyNeighborhoodsResponse = {
    neighborhoods: [{ name: 'Neighborhood1', self: 'n1' }],
  };
  const dummyLanguages: Language[] = [
    { self: 'lang1', name: 'English', displayName: 'English' },
    { self: 'lang2', name: 'Spanish', displayName: 'Español' },
  ];
  const dummyProfessions: Profession[] = [
    { displayName: 'Profession1', self: 'p1', name: 'Profession1' },
  ];

  // Create spies for data-loading services.
  const neighborhoodServiceSpy = jasmine.createSpyObj('NeighborhoodService', [
    'getNeighborhoods',
  ]);
  neighborhoodServiceSpy.getNeighborhoods.and.returnValue(
    of(dummyNeighborhoodsResponse),
  );

  const languageServiceSpy = jasmine.createSpyObj('LanguageService', [
    'getLanguages',
  ]);
  languageServiceSpy.getLanguages.and.returnValue(of(dummyLanguages));

  const professionServiceSpy = jasmine.createSpyObj('ProfessionService', [
    'getProfessions',
  ]);
  professionServiceSpy.getProfessions.and.returnValue(of(dummyProfessions));

  // Other services: we will assign spies for the submission methods.
  const userServiceSpy = jasmine.createSpyObj('UserService', ['createUser']);
  const authServiceSpy = jasmine.createSpyObj('AuthService', [
    'login',
    'isLoggedIn',
  ]);
  const workerServiceSpy = jasmine.createSpyObj('WorkerService', [
    'createWorker',
  ]);
  const linkStorageSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  const userSessionServiceSpy = jasmine.createSpyObj('UserSessionService', [
    'getCurrentRole',
  ]);
  const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // Fake TranslateService.
  const fakeTranslateService = {
    instant: (key: string) => key,
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [SignupDialogComponent, FakeTranslatePipe],
      providers: [
        { provide: NeighborhoodService, useValue: neighborhoodServiceSpy },
        { provide: LanguageService, useValue: languageServiceSpy },
        { provide: ProfessionService, useValue: professionServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: HateoasLinksService, useValue: linkStorageSpy },
        { provide: WorkerService, useValue: workerServiceSpy },
        { provide: UserSessionService, useValue: userSessionServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: TranslateService, useValue: fakeTranslateService },
      ],
      // Override the template to avoid errors with value accessors.
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(SignupDialogComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SignupDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit: loads combos and builds forms.
  });

  it('should submit neighbor signup successfully', fakeAsync(() => {
    // ----- Setup neighbor signup form values -----
    // For neighbor signup, use the signupForm.
    component.signupForm.setValue({
      neighborhood: { self: 'n1', name: 'Neighborhood1' },
      name: 'John',
      surname: 'Doe',
      mail: 'john@example.com',
      password: 'Secret123',
      identification: '1234567',
      language: { self: 'lang1', name: 'English', displayName: 'English' },
    });
    // Simulate that the form is valid.
    expect(component.signupForm.valid).toBeTrue();

    // Stub userService.createUser to return success.
    userServiceSpy.createUser.and.returnValue(of({}));
    // Stub authService.login to return success.
    authServiceSpy.login.and.returnValue(of(true));
    // Simulate user role as NEIGHBOR.
    userSessionServiceSpy.getCurrentRole.and.returnValue(Role.NEIGHBOR);
    // For NEIGHBOR (or ADMINISTRATOR) flow, simulate link lookups.
    linkStorageSpy.getLink.and.callFake((key: string) => {
      if (key === LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL) {
        return 'announcements_channel';
      }
      if (key === LinkKey.NONE_POST_STATUS) {
        return 'none_status';
      }
      return '';
    });
    // Stub router.navigate to return a resolved promise.
    routerSpy.navigate.and.returnValue(Promise.resolve(true));
    // Spy on closeSignupDialog.
    spyOn(component, 'closeSignupDialog');

    // ----- Call the neighbor signup submission method -----
    component.trySignup();
    tick();

    // Expectations:
    // (a) userService.createUser should have been called with correct parameters.
    expect(userServiceSpy.createUser).toHaveBeenCalledWith(
      'n1',
      'John',
      'Doe',
      'Secret123',
      'john@example.com',
      { self: 'lang1', name: 'English', displayName: 'English' },
      '1234567',
    );
    // (b) authService.login should have been called.
    expect(authServiceSpy.login).toHaveBeenCalledWith(
      'john@example.com',
      'Secret123',
    );
    // (c) For a NEIGHBOR, router.navigate should be called with posts and appropriate query params.
    expect(routerSpy.navigate).toHaveBeenCalledWith(['posts'], {
      queryParams: {
        inChannel: 'announcements_channel',
        withStatus: 'none_status',
      },
    });
    // (d) closeSignupDialog should be called.
    expect(component.closeSignupDialog).toHaveBeenCalled();
    // (e) Loading flag should be false.
    expect(component.loading).toBeFalse();
  }));

  it('should submit worker signup successfully', fakeAsync(() => {
    // ----- Setup worker signup form values -----
    // For worker signup, use the serviceForm.
    component.serviceForm.setValue({
      businessName: 'My Business',
      professions: [{ self: 'p1', displayName: 'Profession1' }],
      w_name: 'Alice',
      w_surname: 'Smith',
      w_mail: 'alice@example.com',
      w_password: 'Worker123',
      w_address: '123 Main St',
      w_identification: '7654321',
      w_language: { self: 'lang2', name: 'Spanish', displayName: 'Español' },
      phoneNumber: '+1234567890',
    });
    expect(component.serviceForm.valid).toBeTrue();

    // Stub workerService.createWorker to return success.
    workerServiceSpy.createWorker.and.returnValue(of({}));
    // Stub authService.login to return success.
    authServiceSpy.login.and.returnValue(of(true));
    // Simulate user role as WORKER.
    userSessionServiceSpy.getCurrentRole.and.returnValue(Role.WORKER);
    // For WORKER, simulate link lookup for USER_WORKER.
    linkStorageSpy.getLink.and.callFake((key: string) => {
      if (key === LinkKey.USER_WORKER) {
        return 'worker_url';
      }
      return '';
    });
    routerSpy.navigate.and.returnValue(Promise.resolve(true));
    spyOn(component, 'closeSignupDialog');

    // ----- Call the worker signup submission method -----
    component.submitServiceForm();
    tick();

    // Expectations:
    // (a) workerService.createWorker should be called with the correct parameters.
    expect(workerServiceSpy.createWorker).toHaveBeenCalledWith(
      'Alice',
      'Smith',
      'Worker123',
      'alice@example.com',
      { self: 'lang2', name: 'Spanish', displayName: 'Español' },
      '7654321',
      ['p1'],
      '+1234567890',
      'My Business',
      '123 Main St',
    );
    // (b) authService.login should be called with worker email and password.
    expect(authServiceSpy.login).toHaveBeenCalledWith(
      'alice@example.com',
      'Worker123',
    );
    // (c) For a WORKER, router.navigate should be called with the worker profile route.
    expect(routerSpy.navigate).toHaveBeenCalledWith([
      'services',
      'my-profile',
      encodeUrlSafeBase64('worker_url'),
    ]);
    // (d) closeSignupDialog should be called.
    expect(component.closeSignupDialog).toHaveBeenCalled();
    // (e) Loading flag should be false.
    expect(component.loading).toBeFalse();
  }));

  it('should create the component, initialize both forms, set default values, and load neighborhoods and languages', () => {
    expect(component).toBeTruthy();

    // Default values: selectedOption is 'neighbor' and showSignupDialog is false.
    expect(component.selectedOption).toEqual('neighbor');
    expect(component.showSignupDialog).toBeFalse();

    // Verify neighbor signup form exists and has expected controls.
    expect(component.signupForm).toBeDefined();
    expect(component.signupForm.contains('neighborhood')).toBeTrue();
    expect(component.signupForm.contains('name')).toBeTrue();
    expect(component.signupForm.contains('surname')).toBeTrue();
    expect(component.signupForm.contains('mail')).toBeTrue();
    expect(component.signupForm.contains('password')).toBeTrue();
    expect(component.signupForm.contains('identification')).toBeTrue();
    expect(component.signupForm.contains('language')).toBeTrue();

    // Verify service signup form exists and has expected controls.
    expect(component.serviceForm).toBeDefined();
    expect(component.serviceForm.contains('businessName')).toBeTrue();
    expect(component.serviceForm.contains('professions')).toBeTrue();
    expect(component.serviceForm.contains('w_name')).toBeTrue();
    expect(component.serviceForm.contains('w_surname')).toBeTrue();
    expect(component.serviceForm.contains('w_mail')).toBeTrue();
    expect(component.serviceForm.contains('w_password')).toBeTrue();
    expect(component.serviceForm.contains('w_address')).toBeTrue();
    expect(component.serviceForm.contains('w_identification')).toBeTrue();
    expect(component.serviceForm.contains('w_language')).toBeTrue();
    expect(component.serviceForm.contains('phoneNumber')).toBeTrue();

    // Verify that the dummy neighborhood and language data were loaded.
    expect(component.neighborhoodsList).toEqual(
      dummyNeighborhoodsResponse.neighborhoods,
    );
    expect(component.languages).toEqual(dummyLanguages);
  });
});
