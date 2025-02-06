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

import { NeighborhoodService, Neighborhood, Role } from '@shared/index';
import {
  AuthService,
  ToastService,
  UserSessionService,
  HateoasLinksService,
} from '@core/index';
import { UserService } from '@shared/index';
import { TranslateService } from '@ngx-translate/core';

@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

describe('RejectedPageComponent', () => {
  let component: RejectedPageComponent;
  let fixture: ComponentFixture<RejectedPageComponent>;

  const dummyNeighborhood: Neighborhood = { self: 'n1', name: 'Neighborhood1' };

  const neighborhoodServiceSpy = jasmine.createSpyObj('NeighborhoodService', [
    'getNeighborhoods',
  ]);
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
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
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
    fixture.detectChanges(); 
  });

  it('should handle valid neighborhood signup submission', fakeAsync(() => {
    component.neighborhoodForm.setValue({ neighborhood: dummyNeighborhood });

    userServiceSpy.requestNeighborhood.and.returnValue(of({}));

    component.onSubmit();
    tick();

    expect(component.neighborhoodForm.valid).toBeTrue();
    expect(userServiceSpy.requestNeighborhood).toHaveBeenCalledWith(
      dummyNeighborhood.self,
    );
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
    expect(toastServiceSpy.showToast).toHaveBeenCalledWith(
      'REJECTED-PAGE.SUCCESSFULLY_REQUESTED',
      'success',
    );
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/unverified']);
    expect(component.submitted).toBeTrue();
  }));

  it('should log out and navigate when goBackToMainPage is called', () => {
    component.goBackToMainPage();
    expect(authServiceSpy.logout).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });
});
