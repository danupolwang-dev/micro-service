import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MenuService } from '../../services/menu.service';
import { Observable } from 'rxjs';
import { Menu } from '../../models/menu.model';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  authService = inject(AuthService);
  menuService = inject(MenuService);

  // Observable to hold the menu items
  menus$: Observable<Menu[]>;

  constructor() {
    this.menus$ = this.menuService.menus$;
  }

  logout(): void {
    this.authService.logout();
  }
}
