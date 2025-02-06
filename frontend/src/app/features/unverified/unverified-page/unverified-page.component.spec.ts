import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { UnverifiedPageComponent } from './unverified-page.component';
import { Router } from '@angular/router';
import { AuthService } from '@core/index';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

describe('UnverifiedPageComponent', () => {
  let component: UnverifiedPageComponent;
  let fixture: ComponentFixture<UnverifiedPageComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(waitForAsync(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      declarations: [UnverifiedPageComponent, FakeTranslatePipe],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA], // Ignore other unknown elements if present
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnverifiedPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should call authService.logout when goBackToMainPage is called', () => {
    component.goBackToMainPage();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });
});
