// Shapes of POST /api/auth/login and POST /api/auth/refresh. Field names must match the backend JSON exactly.

// What we send to the backend.
export interface LoginRequest {
  username: string;
  password: string;
}

// What the backend sends back on success (login AND refresh answer with the same shape).
export interface LoginResponse {
  // Short-lived (5 min). Sent on every API call.
  token: string;
  // Long-lived (1 hour). Only sent to /api/auth/refresh, to get a new pair of tokens.
  refreshToken: string;
}

// What we send to /api/auth/refresh.
export interface RefreshRequest {
  refreshToken: string;
}
