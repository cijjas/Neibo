import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ServiceProvidersReviewDialogComponent } from './service-providers-review-dialog.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('ServiceProvidersReviewDialogComponent', () => {
  let component: ServiceProvidersReviewDialogComponent;
  let fixture: ComponentFixture<ServiceProvidersReviewDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ServiceProvidersReviewDialogComponent],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceProvidersReviewDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should initialize with default values and update worker when input is set', () => {
    expect(component.reviewDialogVisible).toBeFalse();

    const dummyWorker = { name: 'Test Worker', id: 'worker1' };
    component.worker = dummyWorker;

    expect(component.worker).toEqual(dummyWorker);
  });
});
