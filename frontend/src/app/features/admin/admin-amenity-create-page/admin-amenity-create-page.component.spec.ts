import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminAmenityCreatePageComponent } from './admin-amenity-create-page.component';
import { AdminModule } from '../admin.module';
import { ShiftService, AmenityService } from '@shared/index';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

describe('AdminAmenityCreatePageComponent', () => {
  let component: AdminAmenityCreatePageComponent;
  let fixture: ComponentFixture<AdminAmenityCreatePageComponent>;

  let mockShiftService: jasmine.SpyObj<ShiftService>;
  let mockAmenityService: jasmine.SpyObj<AmenityService>;

  let httpMock: HttpTestingController;

  beforeEach(async () => {
    // 1) Create spy objects for the services the component uses
    mockShiftService = jasmine.createSpyObj('ShiftService', ['getShifts']);
    mockAmenityService = jasmine.createSpyObj('AmenityService', [
      'createAmenity',
    ]);

    // 2) Provide a mock response for getShifts()
    mockShiftService.getShifts.and.returnValue(
      of([
        {
          startTime: '08:00:00',
          endTime: '10:00:00',
          startTimeDisplay: '8:00 AM',
          endTimeDisplay: '10:00 AM',
          day: 'Monday',
          taken: true,
          self: '/api/shifts/monday-8',
        },
        {
          startTime: '10:00:00',
          endTime: '12:00:00',
          startTimeDisplay: '10:00 AM',
          endTimeDisplay: '12:00 PM',
          day: 'Monday',
          taken: false,
          self: '/api/shifts/monday-10',
        },
      ]),
    );

    // 3) Mock createAmenity as well to prevent real HTTP requests
    mockAmenityService.createAmenity.and.returnValue(of(''));

    await TestBed.configureTestingModule({
      declarations: [AdminAmenityCreatePageComponent],
      imports: [
        AdminModule,
        HttpClientTestingModule, // Helps mock out HTTP requests
        TranslateModule.forRoot(), // Provide translation store
      ],
      providers: [
        { provide: ShiftService, useValue: mockShiftService },
        { provide: AmenityService, useValue: mockAmenityService },
        provideRouter([]), 
      ],
    }).compileComponents();

    // 4) Create the component fixture
    fixture = TestBed.createComponent(AdminAmenityCreatePageComponent);
    component = fixture.componentInstance;

    // 5) Trigger Angular’s change detection, calling ngOnInit() inside the component
    fixture.detectChanges();

    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // 6) Basic test: "does the component exist?"
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
