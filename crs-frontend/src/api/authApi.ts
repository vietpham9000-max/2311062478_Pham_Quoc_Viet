import axiosClient from "./axiosClient";
import type { LoginRequest, LoginResponse } from "../types/auth";

export const login = async (credentials: LoginRequest): Promise<LoginResponse> => {
  const response = await axiosClient.post<LoginResponse>("/api/auth/login", credentials);
  return response.data;
};
