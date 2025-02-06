import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginPageComponent } from './login-page.component';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

describe('LoginPageComponent', () => {
  let component: LoginPageComponent;
  let fixture: ComponentFixture<LoginPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginPageComponent, FakeTranslatePipe],
      schemas: [NO_ERRORS_SCHEMA], // Ignore other unknown elements/attributes.
    }).compileComponents();

    fixture = TestBed.createComponent(LoginPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component and have default dialog flags as false', () => {
    expect(component).toBeTruthy();
    expect(component.showLoginDialog).toBeFalse();
    expect(component.showSignupDialog).toBeFalse();
  });
});
