import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MarketplaceControlBarComponent } from './marketplace-control-bar.component';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { DepartmentService, Department } from '@shared/index';
import { TranslateModule } from '@ngx-translate/core';
import { RouterTestingModule } from '@angular/router/testing';
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
        RouterTestingModule, // Provides routerLink and related directives.
        TranslateModule.forRoot(), // Provides the translate pipe.
      ],
      declarations: [MarketplaceControlBarComponent],
      providers: [
        { provide: DepartmentService, useValue: departmentServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { queryParams: of({ inDepartment: 'dept1' }) },
        },
      ],
      // NO_ERRORS_SCHEMA to ignore any unknown elements.
      schemas: [NO_ERRORS_SCHEMA],
    })
      // Override the template to avoid processing the actual template that uses routerLink etc.
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
    // Provide a dummy department list.
    const mockDepartments: Department[] = [
      { name: 'Electronics', displayName: 'Electronics', self: 'dept1' },
      { name: 'Clothing', displayName: 'Clothing', self: 'dept2' },
    ];
    departmentServiceSpy.getDepartments.and.returnValue(of(mockDepartments));

    fixture.detectChanges(); // Triggers ngOnInit

    expect(component).toBeTruthy();
    // The ActivatedRoute query param 'inDepartment' equals 'dept1' so the component should pick the department with self 'dept1'
    expect(component.departmentName).toEqual('Electronics');
  });
});
