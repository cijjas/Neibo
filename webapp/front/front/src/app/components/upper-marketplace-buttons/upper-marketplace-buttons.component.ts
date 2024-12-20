import { Component, Input } from '@angular/core';
import { Department } from '../../shared/models';

@Component({
  selector: 'app-upper-marketplace-buttons',
  templateUrl: './upper-marketplace-buttons.component.html',
})
export class UpperMarketplaceButtonsComponent {
  @Input() channel!: string;
  @Input() departmentList: Department[] = [];
  @Input() departmentName!: string;

  // You could add logic here if needed
}