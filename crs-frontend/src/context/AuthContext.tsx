/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState, type ReactNode } from "react";
import type { AuthUser, LoginResponse } from "../types/auth";

interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  login: (response: LoginResponse) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const restoreUser = (): AuthUser | null => {
  const storedUser = localStorage.getItem("crs_user");
  const token = localStorage.getItem("crs_token");
  if (!storedUser || !token) return null;

  try {
    const parsed: unknown = JSON.parse(storedUser);
    if (
      typeof parsed === "object" && parsed !== null &&
      "username" in parsed && typeof parsed.username === "string" &&
      "role" in parsed && (parsed.role === "ADMIN" || parsed.role === "STUDENT")
    ) {
      return { username: parsed.username, role: parsed.role };
    }
  } catch {
    // Invalid persisted JSON is cleared below.
  }

  localStorage.removeItem("crs_user");
  return null;
};

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<AuthUser | null>(restoreUser);

  const login = (response: LoginResponse) => {
    const authUser: AuthUser = { username: response.username, role: response.role };
    localStorage.setItem("crs_token", response.token);
    localStorage.setItem("crs_user", JSON.stringify(authUser));
    setUser(authUser);
  };

  const logout = () => {
    localStorage.removeItem("crs_token");
    localStorage.removeItem("crs_user");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: user !== null, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextValue => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth phải được dùng bên trong AuthProvider");
  return context;
};
