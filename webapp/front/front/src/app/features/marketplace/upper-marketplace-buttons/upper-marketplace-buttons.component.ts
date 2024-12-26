import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Department, DepartmentService } from '@shared/index';

@Component({
  selector: 'app-upper-marketplace-buttons',
  templateUrl: './upper-marketplace-buttons.component.html',
})
export class UpperMarketplaceButtonsComponent implements OnInit {
  @Input() channel!: string;
  @Input() departmentList: Department[] = [];
  departmentName: string = 'All';

  constructor(
    private route: ActivatedRoute,
    private departmentService: DepartmentService

  ) { }

  ngOnInit() {

    this.departmentService.getDepartments().subscribe({
      next: (departments) => {
        this.departmentList = departments;

        this.route.queryParams.subscribe((params) => {
          const selectedDepartment = params['inDepartment'];
          this.departmentName = selectedDepartment
            ? this.departmentList.find((dept) => dept.self === selectedDepartment)?.displayName
            : 'All';
        });
      },
      error: (err) => console.error(err)
    });





  }
}
