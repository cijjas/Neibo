import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-success-toast',
  templateUrl: './success-toast.component.html',
})
export class SuccessToastComponent {
  @Input() message: string = '';
  @Input() visible: boolean = false;
  @Input() type: 'success' | 'error' = 'success';
}
