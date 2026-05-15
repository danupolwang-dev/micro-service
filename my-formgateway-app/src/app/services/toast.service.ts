import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface ToastMessage {
    type: 'success' | 'error' | 'info' | 'warning';
    message: string;
    duration?: number;
}

@Injectable({
    providedIn: 'root'
})
export class ToastService {
    private toastSubject = new Subject<ToastMessage>();
    toasts$ = this.toastSubject.asObservable();

    showSuccess(message: string, duration: number = 3000) {
        this.toastSubject.next({ type: 'success', message, duration });
    }

    showError(message: string, duration: number = 5000) {
        this.toastSubject.next({ type: 'error', message, duration });
    }

    showInfo(message: string, duration: number = 3000) {
        this.toastSubject.next({ type: 'info', message, duration });
    }

    showWarning(message: string, duration: number = 4000) {
        this.toastSubject.next({ type: 'warning', message, duration });
    }
}
