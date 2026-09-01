export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  role: "ADMIN" | "STUDENT";
}

export type UserRole = LoginResponse["role"];

export interface AuthUser {
  username: string;
  role: UserRole;
}
