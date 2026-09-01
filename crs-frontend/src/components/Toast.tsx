import { useEffect } from "react";

export type ToastType = "success" | "error";

interface ToastProps {
  message: string;
  type: ToastType;
  onClose: () => void;
}

const Toast = ({ message, type, onClose }: ToastProps) => {
  useEffect(() => {
    const timeout = window.setTimeout(onClose, 4000);
    return () => window.clearTimeout(timeout);
  }, [onClose]);

  return (
    <div className={`toast toast-${type}`} role="status">
      <span>{message}</span>
      <button type="button" onClick={onClose} aria-label="Đóng thông báo">×</button>
    </div>
  );
};

export default Toast;
