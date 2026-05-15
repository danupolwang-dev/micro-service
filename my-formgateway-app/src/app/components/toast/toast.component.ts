import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastMessage, ToastService } from '../../services/toast.service';

@Component({
    selector: 'app-toast',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './toast.component.html',
    styleUrls: ['./toast.component.css']
})
export class ToastComponent implements OnInit {
    toasts: (ToastMessage & { id: number })[] = [];
    private nextId = 0;

    constructor(private toastService: ToastService) { }

    ngOnInit(): void {
        this.toastService.toasts$.subscribe(toast => {
            const id = this.nextId++;
            const newToast = { ...toast, id };
            this.toasts.push(newToast);

            setTimeout(() => {
                this.remove(id);
            }, toast.duration || 3000);
        });
    }

    remove(id: number): void {
        this.toasts = this.toasts.filter(t => t.id !== id);
    }

    getIcon(type: string): string {
        switch (type) {
            case 'success': return 'fa-check-circle';
            case 'error': return 'fa-times-circle';
            case 'warning': return 'fa-exclamation-triangle';
            case 'info': return 'fa-info-circle';
            default: return 'fa-bell';
        }
    }
}
