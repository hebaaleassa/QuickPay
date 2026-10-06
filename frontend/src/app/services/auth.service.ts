import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { finalize, Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse } from '../models/login';

// sessionStorage key. Kept in one place so it is never mistyped.
const TOKEN_KEY = 'token';

// providedIn: 'root' = Angular creates ONE shared instance for the whole app.
@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private http: HttpClient) {}

  // Returns the request so the component can subscribe and react to success / error.
  // tap() runs on success only, so a failed login never saves a token.
  login(username: string, password: string): Observable<LoginResponse> {
    const body: LoginRequest = { username, password };
    return this.http
      .post<LoginResponse>('/api/auth/login', body)
      .pipe(tap((response) => sessionStorage.setItem(TOKEN_KEY, response.token)));
  }

  // Normal logout: only forgets the token in this browser. No request, so the token version
  // does NOT change and the user's other sessions stay logged in.
  logout(): void {
    sessionStorage.removeItem(TOKEN_KEY);
  }

  // "Sign out of all devices": the backend raises our token version (its endpoint is called
  // "logout-all"), so every token of this user stops working. No page uses this yet.
  // finalize() runs on success AND on error, so the local token is removed either way.
  logoutAllDevices(): Observable<void> {
    return this.http.post<void>('/api/auth/logout-all', null).pipe(finalize(() => this.logout()));
  }

  getToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  // Logged in = a token exists AND it has not expired (backend tokens last 60 minutes).
  // The backend still checks the token; this only decides what the UI shows.
  isLoggedIn(): boolean {
    const payload = this.readPayload();
    if (!payload) {
      return false;
    }
    // "exp" is in seconds, Date.now() is in milliseconds.
    return payload.exp * 1000 > Date.now();
  }

  // Roles come from the token claim "roles", e.g. ["TEMPLATE", "ADMIN"].
  getRoles(): string[] {
    return this.readPayload()?.roles ?? [];
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  // Turns a failed login response into the text shown on the login page.
  getErrorMessage(error: HttpErrorResponse): string {
    // The backend answers 401 with an EMPTY body (Spring drops the "Bad credentials" reason),
    // so we decide by status code, not by body text.
    if (error.status === 401) {
      return 'Wrong username or password.';
    }
    // If the backend does send plain text for another error, show it as it is.
    if (typeof error.error === 'string' && error.error) {
      return error.error;
    }
    // Status 0 = the request never reached the backend (it is down, or no internet).
    return 'Cannot reach the server. Please try again.';
  }

  // A JWT is "header.payload.signature". The payload is base64 JSON, so we can read it
  // without any library. We never verify the signature here: that is the backend's job.
  private readPayload(): { exp: number; roles: string[] } | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  }
}

/**
 *
 *
 *
 * i need a static shared version to all users, and a refresh token that update the access token pased in on the shared version.
 * the access token is exp every 5 min, this is used for every req the user send. and the refresh token exp every hour.
 *
 * the shred is only updated when the admin want
 *
 * */


