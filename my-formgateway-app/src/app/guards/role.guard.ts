import { CanActivateFn, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // 1. Check if logged in
  if (!authService.isLoggedIn()) {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }

  // 2. Check for required roles
  const requiredRoles = route.data['roles'] as Array<string>;

  if (!requiredRoles || requiredRoles.length === 0) {
    return true; // No specific roles required, just login
  }

  // Check if user has ANY of the required roles
  const hasRequiredRole = requiredRoles.some(role => authService.hasRole(role));

  if (hasRequiredRole) {
    return true;
  } else {
    // User doesn't have permission
    console.warn('Access denied: User does not have required roles');
    router.navigate(['/dashboard']); // Or an 'access-denied' page
    return false;
  }
};
