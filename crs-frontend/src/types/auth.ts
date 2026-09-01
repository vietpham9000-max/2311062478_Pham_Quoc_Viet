export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  userId: number;
  token: string;
  username: string;
  role: "ADMIN" | "STUDENT";
}

export type UserRole = LoginResponse["role"];

export interface AuthUser {
  userId: number;
  username: string;
  role: UserRole;
}
