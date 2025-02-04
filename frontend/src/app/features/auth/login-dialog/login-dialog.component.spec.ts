import { ChangeDetectorRef, Pipe, PipeTransform } from '@angular/core';
import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
  waitForAsync,
} from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LinkKey, Roles } from '@shared/index';
import {
  AuthService,
  HateoasLinksService,
  UserSessionService,
} from '@core/index';
import { LoginDialogComponent } from './login-dialog.component';

/** Fake translate pipe to support usage of | translate in the template */
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string, ...args: any[]): string {
    return value;
  }
}

describe('LoginDialogComponent', () => {
  let component: LoginDialogComponent;
  let fixture: ComponentFixture<LoginDialogComponent>;

  // Create spies for dependencies.
  const authServiceSpy = jasmine.createSpyObj('AuthService', [
    'isLoggedIn',
    'login',
  ]);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
  const linkStorageSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  const userSessionServiceSpy = jasmine.createSpyObj('UserSessionService', [
    'getCurrentRole',
  ]);
  const cdrSpy = jasmine.createSpyObj('ChangeDetectorRef', ['detectChanges']);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [LoginDialogComponent, FakeTranslatePipe],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: HateoasLinksService, useValue: linkStorageSpy },
        { provide: UserSessionService, useValue: userSessionServiceSpy },
        { provide: ChangeDetectorRef, useValue: cdrSpy },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginDialogComponent);
    component = fixture.componentInstance;
    // Set initial input flags.
    component.showLoginDialog = true;
    component.showSignupDialog = false;
    fixture.detectChanges();
  });

  it('should initialize loginForm on ngOnInit with required controls and validators', () => {
    component.ngOnInit();
    expect(component.loginForm).toBeDefined();
    expect(component.loginForm.contains('email')).toBeTrue();
    expect(component.loginForm.contains('password')).toBeTrue();

    const emailControl = component.loginForm.get('email');
    const passwordControl = component.loginForm.get('password');

    // Email should be required and a valid email.
    emailControl?.setValue('');
    expect(emailControl?.valid).toBeFalse();
    emailControl?.setValue('not-an-email');
    expect(emailControl?.valid).toBeFalse();
    emailControl?.setValue('test@example.com');
    expect(emailControl?.valid).toBeTrue();

    // Password is required.
    passwordControl?.setValue('');
    expect(passwordControl?.valid).toBeFalse();
    passwordControl?.setValue('secret');
    expect(passwordControl?.valid).toBeTrue();
  });

  it('closeLoginDialog and openSignupDialog should update flags and emit events', () => {
    spyOn(component.showLoginDialogChange, 'emit');
    spyOn(component.showSignupDialogChange, 'emit');

    component.closeLoginDialog();
    expect(component.showLoginDialog).toBeFalse();
    expect(component.showLoginDialogChange.emit).toHaveBeenCalledWith(false);

    component.openSignupDialog();
    expect(component.showLoginDialog).toBeFalse();
    expect(component.showLoginDialogChange.emit).toHaveBeenCalledWith(false);
    expect(component.showSignupDialog).toBeTrue();
    expect(component.showSignupDialogChange.emit).toHaveBeenCalledWith(true);
  });

  it('tryLogin should immediately close the login dialog if already logged in', () => {
    // Set isLoggedIn to true.
    authServiceSpy.isLoggedIn.and.returnValue(true);
    // Reset any previous calls on login so we can check that it isn’t called.
    authServiceSpy.login.calls.reset();
    spyOn(component, 'closeLoginDialog');

    component.tryLogin();
    expect(component.closeLoginDialog).toHaveBeenCalled();
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('tryLogin should attempt login when form is valid and navigate for a WORKER', fakeAsync(() => {
    authServiceSpy.isLoggedIn.and.returnValue(false);
    component.ngOnInit();
    component.loginForm.setValue({
      email: 'test@example.com',
      password: 'secret',
    });
    authServiceSpy.login.and.returnValue(of(true));
    userSessionServiceSpy.getCurrentRole.and.returnValue(Roles.WORKER);
    linkStorageSpy.getLink.and.callFake((key: string) => {
      if (key === LinkKey.USER_WORKER) {
        return 'worker_url';
      }
      return '';
    });
    routerSpy.navigate.and.returnValue(Promise.resolve(true));
    spyOn(component, 'closeLoginDialog');

    component.tryLogin();
    tick();

    expect(component.loading).toBeFalse();
    expect(routerSpy.navigate).toHaveBeenCalledWith([
      'services',
      'profile',
      'worker_url',
    ]);
    expect(component.closeLoginDialog).toHaveBeenCalled();
  }));
});
