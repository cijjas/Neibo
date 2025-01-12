import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ConfirmationService {
  private confirmationSubject = new Subject<ConfirmationOptions>();
  private responseSubject = new Subject<boolean>();

  // Method to ask for confirmation
  askForConfirmation(options?: ConfirmationOptions): Observable<boolean> {
    this.confirmationSubject.next(options || {});
    return this.responseSubject.asObservable();
  }

  // Observable to be subscribed by the Confirmation Component
  onConfirm$(): Observable<ConfirmationOptions> {
    return this.confirmationSubject.asObservable();
  }

  // Method to send the user's response
  respondToConfirmation(response: boolean) {
    this.responseSubject.next(response);
  }
}

// Interface for customization options
export interface ConfirmationOptions {
  title?: string;
  message?: string;
  confirmText?: string;
  cancelText?: string;
}
