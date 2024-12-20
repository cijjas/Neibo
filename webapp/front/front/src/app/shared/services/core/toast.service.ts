import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type ToastType = 'success' | 'error';

@Injectable({
    providedIn: 'root',
})
export class ToastService {
    private messageSource = new BehaviorSubject<string>('');
    private typeSource = new BehaviorSubject<ToastType>('success');
    private visibleSource = new BehaviorSubject<boolean>(false);

    message$ = this.messageSource.asObservable();
    type$ = this.typeSource.asObservable();
    visible$ = this.visibleSource.asObservable();

    showToast(message: string, type: ToastType = 'success'): void {
        this.messageSource.next(message);
        this.typeSource.next(type);
        this.visibleSource.next(true);
        console.log('showToast called with:', message, type);

        setTimeout(() => {
            this.visibleSource.next(false);
        }, 3000);
    }
}
