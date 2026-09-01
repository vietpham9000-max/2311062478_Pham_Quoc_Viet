import axiosClient from "./axiosClient";
import type { Registration, RegistrationRequest } from "../types/registration";

export const registerCourse = async (payload: RegistrationRequest): Promise<Registration> => {
  const response = await axiosClient.post<Registration>("/api/registrations", payload);
  return response.data;
};

export const cancelRegistration = async (id: number): Promise<Registration> => {
  const response = await axiosClient.delete<Registration>(`/api/registrations/${id}`);
  return response.data;
};

export const getMyRegistrations = async (): Promise<Registration[]> => {
  const response = await axiosClient.get<Registration[]>("/api/registrations/my");
  return response.data;
};
