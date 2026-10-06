// Shapes of POST /api/auth/login. Field names must match the backend JSON exactly.

// What we send to the backend.
export interface LoginRequest {
  username: string;
  password: string;
}

// What the backend sends back on success: a JWT string.
export interface LoginResponse {
  token: string;
}
