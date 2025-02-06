import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NotFoundPageComponent } from './not-found-page.component';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('NotFoundPageComponent', () => {
  let component: NotFoundPageComponent;
  let fixture: ComponentFixture<NotFoundPageComponent>;

  const fakeActivatedRoute = { queryParams: of({}) };

  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  const fakeTranslateService = { instant: (key: string) => key };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [NotFoundPageComponent],
      providers: [
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: Router, useValue: routerSpy },
        { provide: TranslateService, useValue: fakeTranslateService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(NotFoundPageComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotFoundPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); 
  });

  it('should create and initialize with default error code and message', () => {
    expect(component).toBeTruthy();
    expect(component.errorCode).toEqual('404');
    expect(component.errorMessage).toEqual('');
  });

  it('should navigate to root when goBack is called', () => {
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });
});
