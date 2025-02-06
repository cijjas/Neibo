import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { UserProfilePageComponent } from './user-profile-page.component';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { of } from 'rxjs';
import { SafeUrl } from '@angular/platform-browser';
import { environment } from '../../../../environments/environment';

// Import service types (adjust paths as needed)
import { UserService, User, Role, LinkKey } from '@shared/index';
import {
  ImageService,
  AuthService,
  UserSessionService,
  ToastService,
  HateoasLinksService,
} from '@core/index';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

// (Optional) Define a dummy User interface if not imported.
export interface DummyUser extends User {
  // This example uses only the properties needed for this test.
  darkMode: boolean;
  language: string;
  image: string;
  email: string;
  name: string;
  surname: string;
  phoneNumber: string;
  identification: number;
  creationDate: Date;
  userRole: string;
  userRoleEnum: Role;
  userRoleDisplay: string;
  self: string;
}

// Fake translate pipe to satisfy usage of "| translate" in the template.
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

describe('UserProfilePageComponent - Initialization', () => {
  let component: UserProfilePageComponent;
  let fixture: ComponentFixture<UserProfilePageComponent>;

  // Dummy user for initialization.
  const dummyUser: DummyUser = {
    darkMode: true,
    language: 'spanish_dummy', // will be compared against Spanish key.
    image: 'dummy_image',
    email: 'dummy@example.com',
    name: 'Dummy',
    surname: 'User',
    phoneNumber: '123456789',
    identification: 12345678,
    creationDate: new Date(),
    userRole: '',
    userRoleEnum: Role.WORKER,
    userRoleDisplay: 'Worker',
    self: 'dummy_self',
  };

  // Stub userSessionService to return the dummy user and a WORKER role.
  const userSessionServiceSpy = jasmine.createSpyObj('UserSessionService', [
    'getCurrentUser',
    'getCurrentRole',
  ]);
  userSessionServiceSpy.getCurrentUser.and.returnValue(of(dummyUser));
  userSessionServiceSpy.getCurrentRole.and.returnValue(Role.WORKER);

  // Stub imageService to return a dummy safe URL.
  const imageServiceSpy = jasmine.createSpyObj('ImageService', ['fetchImage']);
  imageServiceSpy.fetchImage.and.returnValue(
    of({ safeUrl: 'dummy_safe_url' as SafeUrl }),
  );

  // Stub link service to return a dummy value for the Spanish language key.
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  linkServiceSpy.getLink.and.callFake((key: string) => {
    if (key === LinkKey.SPANISH_LANGUAGE) {
      return 'spanish_dummy';
    }
    return '';
  });

  // Provide dummy objects for the other services.
  const userServiceSpy = {};
  const authServiceSpy = {};
  const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // Provide a simple fake TranslateService.
  const fakeTranslateService = { instant: (key: string) => key };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [UserProfilePageComponent, FakeTranslatePipe],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: ImageService, useValue: imageServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: UserSessionService, useValue: userSessionServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: TranslateService, useValue: fakeTranslateService },
        { provide: Router, useValue: routerSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfilePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should initialize with dummy user data, load profile image, and set theme based on role', () => {
    // Verify that currentUser is set to the dummy user.
    expect(component.currentUser).toEqual(dummyUser);
    // darkMode should be true.
    expect(component.darkMode).toBeTrue();
    // Since dummyUser.language equals 'spanish_dummy' and linkServiceSpy.getLink returns 'spanish_dummy' for the Spanish key,
    // the language should be set to 'es'.
    expect(component.language).toEqual('es');
    // imageService.fetchImage should have been called with dummyUser.image.
    expect(imageServiceSpy.fetchImage).toHaveBeenCalledWith('dummy_image');
    // The profileImageSafeUrl should be set.
    expect(component.profileImageSafeUrl).toEqual('dummy_safe_url' as SafeUrl);
    // Since the role is WORKER, the theme should be 'services'.
    expect(component.theme).toEqual('services');
    // Also, the environment property should match.
    expect(component.environment).toEqual(environment);
  });
});
