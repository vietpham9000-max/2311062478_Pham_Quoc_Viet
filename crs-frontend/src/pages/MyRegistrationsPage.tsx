import { useCallback, useEffect, useState } from "react";
import {
  cancelRegistration,
  getMyRegistrations,
} from "../api/registrationApi";
import { getCourseById } from "../api/courseApi";
import { getApiErrorMessage } from "../types/apiError";
import type { Registration } from "../types/registration";
import Toast from "../components/Toast";
import { useToast } from "../hooks/useToast";

interface RegistrationRow extends Registration {
  courseName: string;
}

const MyRegistrationsPage = () => {
  const [rows, setRows] = useState<RegistrationRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [cancellingId, setCancellingId] = useState<number | null>(null);

  const { toast, showToast, clearToast } = useToast();

  const visibleRows = rows.filter(
    (registration) => registration.status !== "CANCELLED",
  );

  const loadRegistrations = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const registrations = await getMyRegistrations();

      const mapped = await Promise.all(
        registrations.map(async (registration) => {
          try {
            const course = await getCourseById(registration.courseId);

            return {
              ...registration,
              courseName: course.courseName,
            };
          } catch {
            return {
              ...registration,
              courseName: `Môn #${registration.courseId}`,
            };
          }
        }),
      );

      setRows(mapped);
    } catch (requestError: unknown) {
      setError(getApiErrorMessage(requestError));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    loadRegistrations();
  }, [loadRegistrations]);

  const handleCancel = async (registration: RegistrationRow) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn hủy đăng ký ${registration.courseName}?`,
    );

    if (!confirmed) {
      return;
    }

    setCancellingId(registration.id);

    try {
      await cancelRegistration(registration.id);

      showToast(
        `Hủy đăng ký ${registration.courseName} thành công.`,
        "success",
      );

      await loadRegistrations();
    } catch (requestError: unknown) {
      showToast(getApiErrorMessage(requestError), "error");
    } finally {
      setCancellingId(null);
    }
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <h1>Môn học đã đăng ký</h1>
        <p>Danh sách đăng ký của tài khoản hiện tại</p>
      </header>

      <main className="app-content">
        {loading && (
          <div className="state-message">
            <div className="spinner" />
            <p>Đang tải danh sách đăng ký...</p>
          </div>
        )}

        {!loading && error && (
          <div className="state-message error">
            <p>{error}</p>

            <button
              className="retry-btn"
              type="button"
              onClick={loadRegistrations}
            >
              Thử lại
            </button>
          </div>
        )}

        {!loading && !error && visibleRows.length === 0 && (
          <div className="state-message">
            <p>Bạn chưa đăng ký môn học nào.</p>
          </div>
        )}

        {!loading && !error && visibleRows.length > 0 && (
          <div className="card table-container">
            <table>
              <thead>
                <tr>
                  <th>Tên môn học</th>
                  <th>Ngày đăng ký</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>

              <tbody>
                {visibleRows.map((registration) => (
                  <tr key={registration.id}>
                    <td>{registration.courseName}</td>

                    <td>
                      {new Intl.DateTimeFormat("vi-VN", {
                        dateStyle: "medium",
                        timeStyle: "short",
                      }).format(new Date(registration.registeredAt))}
                    </td>

                    <td>
                      <span
                        className={`registration-status ${registration.status.toLowerCase()}`}
                      >
                        {registration.status}
                      </span>
                    </td>

                    <td>
                      <button
                        className="delete-btn"
                        type="button"
                        disabled={cancellingId !== null}
                        onClick={() => handleCancel(registration)}
                      >
                        {cancellingId === registration.id
                          ? "Đang hủy..."
                          : "Hủy đăng ký"}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>

      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={clearToast}
        />
      )}
    </div>
  );
};

export default MyRegistrationsPage;