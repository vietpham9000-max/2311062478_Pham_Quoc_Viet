import { useState, type FormEvent } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { login as loginApi } from "../api/authApi";
import { useAuth } from "../context/AuthContext";
import { getApiErrorMessage } from "../types/apiError";

const LoginPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const { user, login } = useAuth();
  const navigate = useNavigate();

  if (user) {
    return <Navigate to={user.role === "ADMIN" ? "/admin/courses" : "/courses"} replace />;
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    setError("");
    try {
      const response = await loginApi({ username: username.trim(), password });
      login(response);
      navigate(response.role === "ADMIN" ? "/admin/courses" : "/courses", { replace: true });
    } catch (requestError: unknown) {
      setError(getApiErrorMessage(requestError));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="login-page">
      <section className="login-card">
        <h1>Đăng nhập CRS</h1>
        <p>Đăng nhập để sử dụng chức năng theo vai trò của bạn.</p>
        {error && <div className="alert error-alert">{error}</div>}
        <form onSubmit={handleSubmit}>
          <label>Tên đăng nhập<input type="text" value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" required /></label>
          <label>Mật khẩu<input type="password" value={password} onChange={(event) => setPassword(event.target.value)} autoComplete="current-password" required /></label>
          <button className="primary-btn login-submit" type="submit" disabled={loading}>{loading ? "Đang đăng nhập..." : "Đăng nhập"}</button>
        </form>
      </section>
    </main>
  );
};

export default LoginPage;
