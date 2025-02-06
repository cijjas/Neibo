import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminAmenitiesPageComponent } from './admin-amenities-page.component';
import { AdminModule } from '../admin.module';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { ShiftService } from '@shared/index';
import { of } from 'rxjs';

describe('AdminAmenitiesPageComponent', () => {
  let component: AdminAmenitiesPageComponent;
  let fixture: ComponentFixture<AdminAmenitiesPageComponent>;
  let mockShiftService: jasmine.SpyObj<ShiftService>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    mockShiftService = jasmine.createSpyObj('ShiftService', ['getShifts']);
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
        {
          startTime: '12:00:00',
          endTime: '14:00:00',
          startTimeDisplay: '12:00 PM',
          endTimeDisplay: '2:00 PM',
          day: 'Tuesday',
          taken: true,
          self: '/api/shifts/tuesday-12',
        },
      ]),
    );

    await TestBed.configureTestingModule({
      declarations: [AdminAmenitiesPageComponent],
      imports: [
        AdminModule,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
      ],
      providers: [
        { provide: ShiftService, useValue: mockShiftService },
        {
          provide: ActivatedRoute,
          useValue: { queryParams: of({ page: '1', size: '10' }) },
        },
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminAmenitiesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getShifts() and receive mock data', () => {
    component.loadShifts();
    expect(mockShiftService.getShifts).toHaveBeenCalled();
  });
});
