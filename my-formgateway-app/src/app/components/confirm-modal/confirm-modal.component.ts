import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfirmData, ConfirmService } from '../../services/confirm.service';

@Component({
    selector: 'app-confirm-modal',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './confirm-modal.component.html',
    styleUrls: ['./confirm-modal.component.css']
})
export class ConfirmModalComponent implements OnInit {
    data: ConfirmData | null = null;
    isVisible = false;

    constructor(private confirmService: ConfirmService) { }

    ngOnInit(): void {
        this.confirmService.confirm$.subscribe(data => {
            this.data = data;
            this.isVisible = true;
        });
    }

    handleAction(value: boolean): void {
        if (this.data) {
            this.data.resolve(value);
        }
        this.isVisible = false;
        setTimeout(() => {
            this.data = null;
        }, 300);
    }
}
