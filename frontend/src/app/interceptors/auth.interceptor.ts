import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

// An interceptor runs on EVERY request, so the token is added in one place only.
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.getToken();
  const isLoginRequest = request.url === '/api/auth/login';

  // Step 1: add the token (not for the login request, there is no token yet).
  // A request cannot be changed, so we make a copy with the extra header.
  if (token && !isLoginRequest) {
    request = request.clone({
      setHeaders: { Authorization: 'Bearer ' + token },
    });
  }

  // Step 2: send the request and watch the answer.
  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      // 401 = token expired, so log out and go to the login page.
      // Not for the login request: there 401 only means "Bad credentials".
      if (error.status === 401 && !isLoginRequest) {
        authService.logout();
        router.navigate(['/login']);
      }
      // Give the error back, so the page can still show its own message.
      return throwError(() => error);
    }),
  );
};
