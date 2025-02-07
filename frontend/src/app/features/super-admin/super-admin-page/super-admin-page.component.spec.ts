import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SuperAdminPageComponent } from './super-admin-page.component';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from '@core/index';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { Observable, of } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';

@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

export class FakeTranslateService {
  currentLang = 'en';
  setDefaultLang(lang: string): void {}
  use(lang: string): Observable<string> {
    this.currentLang = lang;
    return of(lang);
  }
  instant(key: string): string {
    return key;
  }
}

describe('SuperAdminPageComponent', () => {
  let component: SuperAdminPageComponent;
  let fixture: ComponentFixture<SuperAdminPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SuperAdminPageComponent, FakeTranslatePipe],
      imports: [HttpClientModule],
      providers: [
        AuthService,
        { provide: TranslateService, useClass: FakeTranslateService },
      ],
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
