import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Department, DepartmentService } from '@shared/index';

@Component({
  selector: 'app-marketplace-control-bar',
  templateUrl: './marketplace-control-bar.component.html',
})
export class MarketplaceControlBarComponent implements OnInit {
  @Input() channel!: string;
  @Input() departmentList: Department[] = [];
  // When no department is selected, we'll set this to "All"
  departmentName: string = 'All';

  constructor(
    private route: ActivatedRoute,
    private departmentService: DepartmentService,
  ) {}

  ngOnInit() {
    this.departmentService.getDepartments().subscribe({
      next: departments => {
        this.departmentList = departments;

        this.route.queryParams.subscribe(params => {
          const selectedDepartment = params['inDepartment'];
          this.departmentName = selectedDepartment
            ? this.departmentList.find(dept => dept.self === selectedDepartment)
                ?.name || 'All'
            : 'All';
        });
      },
      error: err => console.error(err),
    });
  }
}
