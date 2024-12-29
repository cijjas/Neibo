import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class ToastService {
    private toastSubject = new BehaviorSubject<{ message: string; type: 'success' | 'error'; visible: boolean }>({
        message: '',
        type: 'success',
        visible: false,
    });

    toast$ = this.toastSubject.asObservable();

    showToast(message: string, type: 'success' | 'error' = 'success'): void {
        this.toastSubject.next({ message, type, visible: true });
        setTimeout(() => {
            this.toastSubject.next({ message: '', type, visible: false });
        }, 7000);
    }
}
