import { Injectable, inject, effect } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, shareReplay } from 'rxjs';
import { Menu } from '../models/menu.model';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  private menuApiUrl = `${environment.apiUrl}/api/menus`;

  // Use a BehaviorSubject to cache the menu items
  private menusSubject = new BehaviorSubject<Menu[]>([]);
  public menus$ = this.menusSubject.asObservable().pipe(
    shareReplay(1) // Cache the last emitted value
  );

  constructor() {
    // Create an effect that reacts to changes in the isLoggedIn signal
    effect(() => {
      const loggedIn = this.authService.isLoggedIn(); // Read the signal's value
      if (loggedIn) {
        // If logged in, fetch the user's menus
        this.fetchUserMenus().subscribe();
      } else {
        // If logged out, clear the menus
        this.menusSubject.next([]);
      }
    });
  }

  private fetchUserMenus(): Observable<Menu[]> {
    return this.http.get<Menu[]>(`${this.menuApiUrl}/my-menus`).pipe(
      tap(menus => {
        // Sort menus by displayOrder just in case the backend doesn't
        const sortedMenus = menus.sort((a, b) => a.displayOrder - b.displayOrder);
        this.menusSubject.next(sortedMenus);
      })
    );
  }

  /**
   * Refreshes the user's menu. Can be called if roles change during the session.
   */
  public refreshMenus(): void {
    // To refresh, we just need to call fetchUserMenus again
    // Corrected: Call the signal only once to get its boolean value
    if (this.authService.isLoggedIn()) {
      this.fetchUserMenus().subscribe();
    }
  }
}
