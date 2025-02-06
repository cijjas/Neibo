import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminCreateEventComponent } from './admin-create-event.component';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { EventService } from '@shared/index';
import { ToastService } from '@core/index';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('AdminCreateEventComponent', () => {
  let component: AdminCreateEventComponent;
  let fixture: ComponentFixture<AdminCreateEventComponent>;

  // Create spies for the dependencies
  let mockEventService: jasmine.SpyObj<EventService>;
  let mockToastService: jasmine.SpyObj<ToastService>;

  beforeEach(async () => {
    // 1) Create the spy objects
    mockEventService = jasmine.createSpyObj('EventService', ['createEvent']);
    mockToastService = jasmine.createSpyObj('ToastService', ['showToast']);

    // 2) Provide mock return values
    // For createEvent, simulate a successful creation
    mockEventService.createEvent.and.returnValue(of(''));

    await TestBed.configureTestingModule({
      declarations: [AdminCreateEventComponent],
      imports: [
        ReactiveFormsModule,
        TranslateModule.forRoot(), // Provide a basic translate service
      ],
      providers: [
        { provide: EventService, useValue: mockEventService },
        { provide: ToastService, useValue: mockToastService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminCreateEventComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // --------------------------------------------------
  // 1. Basic creation test
  // --------------------------------------------------
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // --------------------------------------------------
  // 2. Ensure form is initialized properly
  // --------------------------------------------------
  it('should have a valid form structure', () => {
    const form = component.eventForm;
    expect(form).toBeTruthy();
    // Check that all controls exist
    expect(form.get('name')).toBeTruthy();
    expect(form.get('description')).toBeTruthy();
    expect(form.get('date')).toBeTruthy();
    expect(form.get('startTime')).toBeTruthy();
    expect(form.get('endTime')).toBeTruthy();

    // Check initial form values
    const formValue = form.value;
    expect(formValue.name).toBe('');
    expect(formValue.description).toBe('');
    expect(formValue.date).toBe('');
    expect(formValue.startTime).toBe('');
    expect(formValue.endTime).toBe('');
  });

  // --------------------------------------------------
  // 3. Validate startBeforeEnd custom validator
  // --------------------------------------------------
  it('should mark form invalid if startTime >= endTime', () => {
    component.eventForm.setValue({
      name: 'Test Event',
      description: 'Desc',
      date: '2025-01-01',
      startTime: '10:00', 
      endTime: '09:00', 
    });
    expect(component.eventForm.invalid).toBeTrue();
    expect(component.eventForm.errors).toEqual({ startBeforeEnd: true });
  });

  it('should be valid if startTime < endTime', () => {
    component.eventForm.setValue({
      name: 'Test Event',
      description: 'Desc',
      date: '2025-01-01',
      startTime: '09:00',
      endTime: '10:00',
    });
    expect(component.eventForm.valid).toBeTrue();
    expect(component.eventForm.errors).toBeNull();
  });

  // --------------------------------------------------
  // 4. onSubmit: invalid form scenario
  // --------------------------------------------------
  it('should mark form touched and return if invalid', () => {
    spyOn(component.eventForm, 'markAllAsTouched');
    component.eventForm.setValue({
      name: '', 
      description: '', 
      date: '', 
      startTime: '', 
      endTime: '', 
    });
    component.onSubmit();

    expect(component.eventForm.markAllAsTouched).toHaveBeenCalled();
    // Should not call createEvent if form invalid
    expect(mockEventService.createEvent).not.toHaveBeenCalled();
  });

  // --------------------------------------------------
  // 5. onSubmit: valid form scenario
  // --------------------------------------------------
  it('should call createEvent with correct arguments and reset form on success', () => {
    // Spy on the form reset to confirm it was called
    spyOn(component.eventForm, 'reset').and.callThrough();

    component.eventForm.setValue({
      name: 'Some Event',
      description: 'Event Description',
      date: '2025-01-01',
      startTime: '09:00',
      endTime: '10:00',
    });

    component.onSubmit(); // Now it should pass validation

    // Check that createEvent was called with the correct arguments
    expect(mockEventService.createEvent).toHaveBeenCalledWith(
      'Some Event',
      'Event Description',
      new Date('2025-01-01T00:00:00Z'),
      '09:00:00', 
      '10:00:00',
    );

    // The form should be reset
    expect(component.eventForm.reset).toHaveBeenCalled();

    // Toast success should be shown
    expect(mockToastService.showToast).toHaveBeenCalled();
  });

  // --------------------------------------------------
  // 6. ensureSeconds() helper
  // --------------------------------------------------
  it('should append :00 if missing seconds', () => {
    expect(component['ensureSeconds']('09:15')).toBe('09:15:00');
    expect(component['ensureSeconds']('09:15:30')).toBe('09:15:30');
    expect(component['ensureSeconds']('0915')).toBe('0915'); 
  });
});
