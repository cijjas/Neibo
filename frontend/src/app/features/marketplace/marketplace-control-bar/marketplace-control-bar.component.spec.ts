import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MarketplaceControlBarComponent } from './marketplace-control-bar.component';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { DepartmentService, Department } from '@shared/index';
import { TranslateModule } from '@ngx-translate/core';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('MarketplaceControlBarComponent', () => {
  let component: MarketplaceControlBarComponent;
  let fixture: ComponentFixture<MarketplaceControlBarComponent>;
  let departmentServiceSpy: jasmine.SpyObj<DepartmentService>;

  beforeEach(waitForAsync(() => {
    departmentServiceSpy = jasmine.createSpyObj('DepartmentService', [
      'getDepartments',
    ]);

    TestBed.configureTestingModule({
      imports: [
        TranslateModule.forRoot(), 
      ],
      declarations: [MarketplaceControlBarComponent],
      providers: [
        { provide: DepartmentService, useValue: departmentServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { queryParams: of({ inDepartment: 'dept1' }) },
        },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(
        MarketplaceControlBarComponent,
        `<div>Dummy Template</div>`,
      )
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketplaceControlBarComponent);
    component = fixture.componentInstance;
  });

  it('should create and initialize with default department', () => {
    const mockDepartments: Department[] = [
      { name: 'Electronics', displayName: 'Electronics', self: 'dept1' },
      { name: 'Clothing', displayName: 'Clothing', self: 'dept2' },
    ];
    departmentServiceSpy.getDepartments.and.returnValue(of(mockDepartments));

    fixture.detectChanges(); 

    expect(component).toBeTruthy();
    expect(component.departmentName).toEqual('Electronics');
  });
});
