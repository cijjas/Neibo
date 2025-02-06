import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SuperAdminPageComponent } from './super-admin-page.component';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from '@core/index';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

describe('SuperAdminPageComponent', () => {
  let component: SuperAdminPageComponent;
  let fixture: ComponentFixture<SuperAdminPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SuperAdminPageComponent, FakeTranslatePipe],
      imports: [HttpClientModule],
      providers: [AuthService],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(SuperAdminPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
