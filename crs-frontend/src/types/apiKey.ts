export type ApiKeyStatus = "ACTIVE" | "REVOKED";
export interface ApiKeyRecord { id: number; keyValue: string; ownerName: string; scopes: string; status: ApiKeyStatus; expiresAt: string | null; createdAt: string; }
export interface ApiKeyCreatePayload { ownerName: string; scopes: string; validDays: number | null; }
