import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Check if running in a browser environment
  if (typeof window !== 'undefined') {
    const authToken = localStorage.getItem('authToken');

    if (authToken) {
      // Clone the request and add the authorization header
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${authToken}`
        }
      });
      return next(authReq);
    }
  }

  // If there's no token, pass the original request along
  return next(req);
};
