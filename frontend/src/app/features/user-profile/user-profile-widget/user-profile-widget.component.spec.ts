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

import { User, Role } from '@shared/index';
import {
  UserSessionService,
  ImageService,
  HateoasLinksService,
} from '@core/index';
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

  const userSessionServiceSpy = jasmine.createSpyObj('UserSessionService', [
    'getCurrentUser',
    'getCurrentRole',
  ]);
  userSessionServiceSpy.getCurrentUser.and.returnValue(of(dummyUser));
  userSessionServiceSpy.getCurrentRole.and.returnValue(Role.NEIGHBOR);

  const imageServiceSpy = jasmine.createSpyObj('ImageService', ['fetchImage']);
  imageServiceSpy.fetchImage.and.returnValue(
    of({ safeUrl: 'dummy_safe_url' as SafeUrl }),
  );

  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);

  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  const fakeTranslateService = {
    instant: (key: string) => key,
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
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
    expect(component.currentUser).toEqual(dummyUser);

    expect(imageServiceSpy.fetchImage).toHaveBeenCalledWith(dummyUser.image);
    tick(); // Flush asynchronous observables.
    expect(component.profileImageSafeUrl).toEqual('dummy_safe_url' as SafeUrl);

    component.navigateToProfile();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/profile']);
  }));
});
