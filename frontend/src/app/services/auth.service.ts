import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { finalize, Observable, shareReplay, tap } from 'rxjs';
import { LoginRequest, LoginResponse, RefreshRequest } from '../models/login';

// sessionStorage keys. Kept in one place so they are never mistyped.
const TOKEN_KEY = 'token';
const REFRESH_TOKEN_KEY = 'refreshToken';

// providedIn: 'root' = Angular creates ONE shared instance for the whole app.
@Injectable({ providedIn: 'root' })
export class AuthService {
  // The refresh request that is running right now, if any. See refresh() for why.
  private refreshInFlight: Observable<LoginResponse> | null = null;

  constructor(private http: HttpClient) {}

  // Returns the request so the component can subscribe and react to success / error.
  // tap() runs on success only, so a failed login never saves a token.
  login(username: string, password: string): Observable<LoginResponse> {
    const body: LoginRequest = { username, password };
    return this.http
      .post<LoginResponse>('/api/auth/login', body)
      .pipe(tap((response) => this.saveTokens(response)));
  }

  // Trades the refresh token for a new pair of tokens (the access token lives only 5 minutes).
  // If 3 requests get a 401 at the same moment, all 3 call this. We share ONE running request
  // between them, so we make 1 refresh call instead of 3 and save the new tokens only once.
  refresh(): Observable<LoginResponse> {
    if (!this.refreshInFlight) {
      const body: RefreshRequest = { refreshToken: this.getRefreshToken() ?? '' };
      this.refreshInFlight = this.http.post<LoginResponse>('/api/auth/refresh', body).pipe(
        tap((response) => this.saveTokens(response)),
        // Done (success or error): the next expiry may start a fresh refresh.
        finalize(() => (this.refreshInFlight = null)),
        // Everyone who subscribes gets the same answer instead of sending a new request.
        shareReplay(1),
      );
    }
    return this.refreshInFlight;
  }

  // Normal logout: only forgets the tokens in this browser. No request, so the token version
  // does NOT change and the user's other sessions stay logged in.
  logout(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(REFRESH_TOKEN_KEY);
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

  getRefreshToken(): string | null {
    return sessionStorage.getItem(REFRESH_TOKEN_KEY);
  }

  // Logged in = the REFRESH token exists and has not expired (1 hour). We do not look at the access
  // token here: it expires every 5 minutes, but the interceptor quietly renews it when needed.
  // The backend still checks the token; this only decides what the UI shows.
  isLoggedIn(): boolean {
    const payload = this.readPayload(this.getRefreshToken());
    if (!payload) {
      return false;
    }
    // "exp" is in seconds, Date.now() is in milliseconds.
    return payload.exp * 1000 > Date.now();
  }

  // Roles come from the token claim "roles", e.g. ["TEMPLATE", "ADMIN"].
  getRoles(): string[] {
    return this.readPayload(this.getToken())?.roles ?? [];
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  private saveTokens(response: LoginResponse): void {
    sessionStorage.setItem(TOKEN_KEY, response.token);
    sessionStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken);
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
  private readPayload(token: string | null): { exp: number; roles: string[] } | null {
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


