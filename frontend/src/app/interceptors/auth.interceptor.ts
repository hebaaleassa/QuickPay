import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

// A request cannot be changed, so we make a copy with the extra header.
function withToken(request: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return request.clone({ setHeaders: { Authorization: 'Bearer ' + token } });
}

// An interceptor runs on EVERY request, so the token is added in one place only.
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.getToken();
  // Login and refresh must go out WITHOUT the Authorization header: they are how we get tokens.
  const isAuthRequest = request.url === '/api/auth/login' || request.url === '/api/auth/refresh';

  // Step 1: add the token (not for login / refresh, there is no valid token yet).
  if (token && !isAuthRequest) {
    request = withToken(request, token);
  }

  // Step 2: send the request and watch the answer.
  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      // 401 on a normal request = the 5-minute access token probably expired.
      // (On login it only means "Bad credentials", on refresh it means the refresh token is dead.)
      if (error.status === 401 && !isAuthRequest) {
        // TODO(human): instead of logging out right away, try to renew the token first.
        // Call authService.refresh(), then re-send this request ONCE with the new token
        // (use withToken(request, response.token) and next(...)). If the refresh itself fails,
        // THEN do what the two lines below do. See the comment in the reply for hints.
        authService.logout();
        router.navigate(['/login']);
      }
      // Give the error back, so the page can still show its own message.
      return throwError(() => error);
    }),
  );
};
