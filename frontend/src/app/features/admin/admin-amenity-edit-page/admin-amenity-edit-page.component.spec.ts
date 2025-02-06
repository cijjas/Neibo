import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminAmenityEditPageComponent } from './admin-amenity-edit-page.component';
import { AdminModule } from '../admin.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateModule } from '@ngx-translate/core';
import { ShiftService, AmenityService, Shift } from '@shared/index';
import { ToastService } from '@core/index';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';

describe('AdminAmenityEditPageComponent', () => {
  let component: AdminAmenityEditPageComponent;
  let fixture: ComponentFixture<AdminAmenityEditPageComponent>;
  let mockShiftService: jasmine.SpyObj<ShiftService>;
  let mockAmenityService: jasmine.SpyObj<AmenityService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    // 1. Create spy objects for the services the component uses
    mockShiftService = jasmine.createSpyObj('ShiftService', ['getShifts']);
    mockAmenityService = jasmine.createSpyObj('AmenityService', [
      'updateAmenity',
    ]);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    // 2. Prepare mock data
    const mockAmenity = {
      name: 'Gym',
      description: 'A place to work out',
      availableShifts: [
        {
          day: 'Monday',
          startTime: '08:00:00',
          endTime: '10:00:00',
          taken: false,
          self: '/api/shifts/1',
        },
      ],
      self: '/api/amenities/123',
    };

    const mockShifts: Shift[] = [
      {
        day: 'Monday',
        startTime: '08:00:00',
        endTime: '10:00:00',
        taken: false,
        self: '/api/shifts/1',
      },
      {
        day: 'Tuesday',
        startTime: '10:00:00',
        endTime: '12:00:00',
        taken: true,
        self: '/api/shifts/2',
      },
    ];

    // 3. Configure the spies
    mockShiftService.getShifts.and.returnValue(of(mockShifts));
    mockAmenityService.updateAmenity.and.returnValue(
      of({
        name: 'Gym',
        description: 'A place to work out',
        availableShifts: [
          {
            day: 'Monday',
            startTime: '08:00:00',
            endTime: '10:00:00',
            taken: false,
            self: '/api/shifts/1',
          },
        ],
        self: '/api/amenities/123',
      }),
    );

    // 4. Configure the testing module
    await TestBed.configureTestingModule({
      declarations: [AdminAmenityEditPageComponent],
      imports: [
        AdminModule,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
      ],
      providers: [
        { provide: ShiftService, useValue: mockShiftService },
        { provide: AmenityService, useValue: mockAmenityService },
        { provide: Router, useValue: mockRouter },
        {
          provide: ActivatedRoute,
          useValue: {
            // Simulate route data containing the amenity
            data: of({ amenity: mockAmenity }),
            snapshot: { params: { id: '123' } },
          },
        },
      ],
    }).compileComponents();

    // 5. Create the component and trigger ngOnInit()
    fixture = TestBed.createComponent(AdminAmenityEditPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load amenity data and shifts on init', () => {
    // After ngOnInit, the component should have loaded the amenity from route.data
    expect(component.amenityName).toBe('Gym');
    expect(component.amenityForm.value.name).toBe('Gym');
    expect(component.amenityForm.value.description).toBe('A place to work out');

    // Verify that getShifts was called and that selectedShifts are set correctly.
    expect(mockShiftService.getShifts).toHaveBeenCalled();
    // The amenity.availableShifts includes one shift with self '/api/shifts/1'
    // and mockShifts has that shift, so selectedShifts should have one element.
    expect(component.selectedShifts.length).toBe(1);
  });

  it('should update an amenity on submit when form is valid and shifts are selected', () => {
    // Set the form to valid values.
    component.amenityForm.setValue({
      name: 'Updated Gym',
      description: 'Updated description',
    });
    // Ensure there is at least one selected shift.
    component.selectedShifts = [
      {
        day: 'Monday',
        startTime: '08:00:00',
        endTime: '10:00:00',
        taken: false,
        self: '/api/shifts/1',
      },
    ];

    // Call the submit handler.
    component.onSubmit();

    // Expect the amenityService.updateAmenity to be called with:
    // - id from ActivatedRoute.snapshot.params
    // - the form values
    // - an array of selected shift self URLs.
    expect(mockAmenityService.updateAmenity).toHaveBeenCalledWith(
      '123',
      'Updated Gym',
      'Updated description',
      ['/api/shifts/1'],
    );

    // Expect the router to navigate to 'admin/amenities'
    expect(mockRouter.navigate).toHaveBeenCalledWith(['admin/amenities']);
  });
});
