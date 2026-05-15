import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../environments/environment'; // Corrected path

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authApiUrl = `${environment.apiUrl}/api/auth`;

  // Signal to track login status reactively
  isLoggedIn = signal<boolean>(this.hasToken());

  // Signal to track user roles
  userRoles = signal<string[]>(this.getRolesFromToken());

  constructor(private http: HttpClient, private router: Router) { }

  private hasToken(): boolean {
    // Check for token in localStorage on service initialization
    return typeof window !== 'undefined' && !!localStorage.getItem('authToken');
  }

  private getRolesFromToken(): string[] {
    if (typeof window === 'undefined') return [];

    const token = localStorage.getItem('authToken');
    if (!token) return [];

    try {
      const decoded: any = jwtDecode(token);
      // Assuming the backend sends roles in a 'roles' claim which is an array of strings
      return decoded.roles || [];
    } catch (error) {
      console.error('Error decoding token', error);
      return [];
    }
  }

  register(user: any): Observable<any> {
    return this.http.post<any>(`${this.authApiUrl}/signup`, user);
  }

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.authApiUrl}/signin`, credentials).pipe(
      tap(response => {
        if (response.accessToken) {
          localStorage.setItem('authToken', response.accessToken);
          this.isLoggedIn.set(true); // Update login status

          // Decode token and update roles
          const roles = this.getRolesFromToken();
          this.userRoles.set(roles);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('authToken');
    this.isLoggedIn.set(false); // Update login status
    this.userRoles.set([]); // Clear roles
    this.router.navigate(['/login']); // Redirect to login page
  }

  hasRole(role: string): boolean {
    const roles = this.userRoles();
    // Check if any role in the list matches the requested role (with or without ROLE_ prefix)
    return roles.some(r => r === role || r === `ROLE_${role}`);
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }
}
