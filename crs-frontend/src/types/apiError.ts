import axios from "axios";

export interface ApiError {
  timestamp?: string;
  status: number;
  error: string;
  message: string;
  path?: string;
  errors?: Record<string, string>;
}


const STATUS_MESSAGES: Record<number, string> = {
  400: "Dữ liệu không hợp lệ.",
  401: "Token không hợp lệ hoặc đã hết hạn.",
  403: "Bạn không có quyền thực hiện thao tác này.",
  409: "Dữ liệu bị trùng hoặc xung đột.",
  500: "Lỗi máy chủ.",
};

export const getApiErrorMessage = (error: unknown): string => {
  if (!axios.isAxiosError<ApiError>(error)) {
    return error instanceof Error ? error.message : "Đã xảy ra lỗi không xác định.";
  }

  const backendMessage = error.response?.data?.message;
  if (backendMessage) return backendMessage;

  const status = error.response?.status;
  if (status && STATUS_MESSAGES[status]) return STATUS_MESSAGES[status];

  return "Không thể kết nối đến máy chủ.";
};
