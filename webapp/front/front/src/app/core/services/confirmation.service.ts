import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';



// Interface for customization options
export interface ConfirmationOptions {
  title?: string;
  message?: string;
  confirmText?: string;
  cancelText?: string;
}


@Injectable({
  providedIn: 'root',
})
export class ConfirmationService {
  private confirmationSubject = new Subject<ConfirmationOptions>();

  private currentResponseSubject?: Subject<boolean>;

  // Ask for confirmation
  askForConfirmation(options?: ConfirmationOptions): Observable<boolean> {
    this.currentResponseSubject = new Subject<boolean>();
    this.confirmationSubject.next(options || {});
    return this.currentResponseSubject.asObservable();
  }
  // *when* it needs to show a dialog, and *what text* to show
  onConfirm$(): Observable<ConfirmationOptions> {
    return this.confirmationSubject.asObservable();
  }

  // Called by the dialog's confirm/cancel methods
  respondToConfirmation(response: boolean) {
    if (this.currentResponseSubject) {
      this.currentResponseSubject.next(response);
      this.currentResponseSubject.complete();
      this.currentResponseSubject = undefined;
    }
  }
}
