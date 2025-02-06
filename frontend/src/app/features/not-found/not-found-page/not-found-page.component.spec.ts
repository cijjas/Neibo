import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NotFoundPageComponent } from './not-found-page.component';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('NotFoundPageComponent', () => {
  let component: NotFoundPageComponent;
  let fixture: ComponentFixture<NotFoundPageComponent>;

  // Provide a fake ActivatedRoute that emits an empty queryParams object.
  const fakeActivatedRoute = { queryParams: of({}) };

  // Spy on Router.navigate.
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // Fake TranslateService that returns the key passed to instant().
  const fakeTranslateService = { instant: (key: string) => key };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      // Declare the component.
      declarations: [NotFoundPageComponent],
      providers: [
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: Router, useValue: routerSpy },
        { provide: TranslateService, useValue: fakeTranslateService },
      ],
      // Use NO_ERRORS_SCHEMA to ignore other unknown elements.
      schemas: [NO_ERRORS_SCHEMA],
    })
      // Override the component template to bypass the translate pipe.
      .overrideTemplate(NotFoundPageComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotFoundPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
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
