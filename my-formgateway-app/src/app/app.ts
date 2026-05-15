import { Component, signal, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { LoadingService } from './services/loading.service';
import { CommonModule } from '@angular/common';
import { ToastComponent } from './components/toast/toast.component';
import { ConfirmModalComponent } from './components/confirm-modal/confirm-modal.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule, // Needed for *ngIf
    RouterOutlet,
    NavbarComponent,
    ToastComponent,
    ConfirmModalComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('my-formgateway-app');
  public loadingService = inject(LoadingService);
}
