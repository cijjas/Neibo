import { Component } from '@angular/core';
import { ToastService } from '@core/index';

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

  hideToast() {
    this.visible = false; // Trigger CSS transition
    setTimeout(() => {
      this.message = ''; // Clear the message after transition ends
    }, 400); // Matches CSS transition duration (400ms)
  }
}
