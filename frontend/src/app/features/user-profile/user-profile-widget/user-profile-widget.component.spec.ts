import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
  waitForAsync,
} from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';
import { SafeUrl } from '@angular/platform-browser';
import { Router } from '@angular/router';

// Import service types (adjust paths as needed)
import { User, Role } from '@shared/index';
import {
  UserSessionService,
  ImageService,
  HateoasLinksService,
} from '@core/index';
// Import the standalone component using its module import.
import { UserProfileWidgetComponent } from './user-profile-widget.component';
import { TranslateService } from '@ngx-translate/core';

describe('UserProfileWidgetComponent - Toggles and Image', () => {
  let component: UserProfileWidgetComponent;
  let fixture: ComponentFixture<UserProfileWidgetComponent>;

  // Dummy user for testing.
  const dummyUser: User = {
    darkMode: true,
    language: 'en',
    image: 'dummy_image_url',
    email: 'dummy@example.com',
    name: 'Dummy',
    surname: 'User',
    phoneNumber: '1234567890',
    identification: 12345678,
    creationDate: new Date(),
    userRole: '',
    userRoleEnum: Role.NEIGHBOR,
    userRoleDisplay: 'Neighbor',
    self: 'dummy_self',
  };

  // Stub UserSessionService: getCurrentUser returns dummyUser.
  const userSessionServiceSpy = jasmine.createSpyObj('UserSessionService', [
    'getCurrentUser',
    'getCurrentRole',
  ]);
  userSessionServiceSpy.getCurrentUser.and.returnValue(of(dummyUser));
  // For this test, return any role (e.g., NEIGHBOR).
  userSessionServiceSpy.getCurrentRole.and.returnValue(Role.NEIGHBOR);

  // Stub ImageService: fetchImage returns a dummy safe URL.
  const imageServiceSpy = jasmine.createSpyObj('ImageService', ['fetchImage']);
  imageServiceSpy.fetchImage.and.returnValue(
    of({ safeUrl: 'dummy_safe_url' as SafeUrl }),
  );

  // Stub HateoasLinksService (not used in this test).
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);

  // Stub Router.
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // Provide a simple fake TranslateService.
  const fakeTranslateService = {
    instant: (key: string) => key,
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      // Import the standalone component.
      imports: [UserProfileWidgetComponent],
      providers: [
        { provide: UserSessionService, useValue: userSessionServiceSpy },
        { provide: ImageService, useValue: imageServiceSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: TranslateService, useValue: fakeTranslateService },
        { provide: Router, useValue: routerSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileWidgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should initialize with dummy user data, load profile image, and navigate to profile', fakeAsync(() => {
    // Verify that ngOnInit sets currentUser from the userSessionService.
    expect(component.currentUser).toEqual(dummyUser);

    // Verify that imageService.fetchImage was called with dummyUser.image.
    expect(imageServiceSpy.fetchImage).toHaveBeenCalledWith(dummyUser.image);
    tick(); // Flush asynchronous observables.
    expect(component.profileImageSafeUrl).toEqual('dummy_safe_url' as SafeUrl);

    // Test that navigateToProfile() calls router.navigate with ['/profile'].
    component.navigateToProfile();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/profile']);
  }));
});
