import axiosClient from "./axiosClient";
import type { ApiKeyCreatePayload, ApiKeyRecord } from "../types/apiKey";
export const getApiKeys = async () => (await axiosClient.get<ApiKeyRecord[]>("/api/api-keys")).data;
export const createApiKey = async (payload: ApiKeyCreatePayload) => (await axiosClient.post<ApiKeyRecord>("/api/api-keys", payload)).data;
export const revokeApiKey = async (id: number) => (await axiosClient.delete<ApiKeyRecord>(`/api/api-keys/${id}`)).data;
