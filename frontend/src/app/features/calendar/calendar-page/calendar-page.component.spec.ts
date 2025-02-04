import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CalendarPageComponent } from './calendar-page.component';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('CalendarPageComponent', () => {
  let component: CalendarPageComponent;
  let fixture: ComponentFixture<CalendarPageComponent>;

  // Fake ActivatedRoute with a query param "date" set to "2022-12-25"
  const fakeActivatedRoute = {
    queryParams: of({ date: '2022-12-25' }),
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CalendarPageComponent],
      providers: [{ provide: ActivatedRoute, useValue: fakeActivatedRoute }],
      // Use NO_ERRORS_SCHEMA to ignore unknown elements and pipes.
      schemas: [NO_ERRORS_SCHEMA],
    })
      // Override the template to a dummy one so that the translate pipe is never used.
      .overrideTemplate(CalendarPageComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CalendarPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should initialize with the provided date query param', () => {
    // Expected date: December 25, 2022 at noon (months are 0-indexed)
    const expectedDate = new Date(2022, 11, 25, 12);
    expect(component.selectedDate.getTime()).toEqual(expectedDate.getTime());

    const options: Intl.DateTimeFormatOptions = {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    };
    const expectedFormatted = expectedDate.toLocaleDateString('en-US', options);
    expect(component.formattedSelectedDate).toEqual(expectedFormatted);
  });
});
