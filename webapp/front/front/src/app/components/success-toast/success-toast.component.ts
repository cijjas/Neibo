import { Component } from '@angular/core';
import { ToastService } from '../../shared/services/index.service';

@Component({
  selector: 'app-success-toast',
  templateUrl: './success-toast.component.html',
})
export class SuccessToastComponent {
  message: string = '';
  visible: boolean = false;
  type: 'success' | 'error' = 'success';

  constructor(private toastService: ToastService) {
    this.toastService.toast$.subscribe((toast) => {
      this.message = toast.message;
      this.type = toast.type;
      this.visible = toast.visible;
    });
  }

  get toastClasses() {
    return {
      show: this.visible,
      [this.type]: true,
    };
  }
}
