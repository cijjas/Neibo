import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error' | 'warning';
}

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private toastsSubject = new BehaviorSubject<Toast[]>([]);
  public toasts$ = this.toastsSubject.asObservable();

  private counter = 0;

  showToast(
    message: string,
    type: 'success' | 'error' | 'warning' = 'success',
  ): void {
    this.counter++;
    const newToast: Toast = {
      id: this.counter,
      message,
      type,
    };

    // Push the new toast
    const updatedToasts = [...this.toastsSubject.value, newToast];
    this.toastsSubject.next(updatedToasts);

    // Remove the toast after 7 seconds
    setTimeout(() => {
      this.removeToast(newToast.id);
    }, 7000);
  }

  removeToast(toastId: number): void {
    const updatedToasts = this.toastsSubject.value.filter(
      t => t.id !== toastId,
    );
    this.toastsSubject.next(updatedToasts);
  }
}
