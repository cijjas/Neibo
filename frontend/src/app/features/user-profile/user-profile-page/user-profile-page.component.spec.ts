import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { UserProfilePageComponent } from './user-profile-page.component';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { of } from 'rxjs';
import { SafeUrl } from '@angular/platform-browser';
import { environment } from '../../../../environments/environment';

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

export interface DummyUser extends User {
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

@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

describe('UserProfilePageComponent - Initialization', () => {
  let component: UserProfilePageComponent;
  let fixture: ComponentFixture<UserProfilePageComponent>;

  const dummyUser: DummyUser = {
    darkMode: true,
    language: 'spanish_dummy',
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

  const userSessionServiceSpy = jasmine.createSpyObj('UserSessionService', [
    'getCurrentUser',
    'getCurrentUserValue',
    'getCurrentRole',
  ]);
  userSessionServiceSpy.getCurrentUser.and.returnValue(of(dummyUser));
  userSessionServiceSpy.getCurrentRole.and.returnValue(Role.WORKER);

  const imageServiceSpy = jasmine.createSpyObj('ImageService', ['fetchImage']);
  imageServiceSpy.fetchImage.and.returnValue(
    of({ safeUrl: 'dummy_safe_url' as SafeUrl }),
  );

  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  linkServiceSpy.getLink.and.callFake((key: string) => {
    if (key === LinkKey.SPANISH_LANGUAGE) {
      return 'spanish_dummy';
    }
    return '';
  });

  const userServiceSpy = {};
  const authServiceSpy = {};
  const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

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
    expect(component.currentUser).toEqual(dummyUser);
    expect(component.darkMode).toBeTrue();
    expect(component.language).toEqual('es');
    expect(imageServiceSpy.fetchImage).toHaveBeenCalledWith('dummy_image');
    expect(component.profileImageSafeUrl).toEqual('dummy_safe_url' as SafeUrl);
    expect(component.theme).toEqual('services');
    expect(component.environment).toEqual(environment);
  });
});
