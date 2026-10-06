import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
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
        // Renew the tokens first, then send the SAME request again with the new access token.
        // switchMap = "when the refresh answers, replace it with this other request".
        // Retrying is not a loop: the retry goes through next(), not through this catchError,
        // so a second 401 is returned to the page as a normal error.
        return authService.refresh().pipe(
          // Placed BEFORE switchMap on purpose: only a failed refresh logs out. If the retried
          // request fails (e.g. 403), the page just gets that error and the user stays logged in.
          catchError((refreshError) => {
            authService.logout();
            router.navigate(['/login']);
            return throwError(() => refreshError);
          }),
          switchMap((response) => next(withToken(request, response.token))),
        );
      }
      // Give the error back, so the page can still show its own message.
      return throwError(() => error);
    }),
  );
};
