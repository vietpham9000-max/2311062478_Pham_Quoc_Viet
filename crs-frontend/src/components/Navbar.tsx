import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <Link className="navbar-brand" to="/courses">CRS</Link>
      <div className="navbar-links">
        <NavLink to="/courses">Khóa học</NavLink>
        {user?.role === "ADMIN" && <NavLink to="/admin/courses">Quản trị môn học</NavLink>}
        {user?.role === "STUDENT" && <NavLink to="/register-course">Đăng ký khóa học</NavLink>}
      </div>
      <div className="navbar-user">
        {user ? (
          <>
            <span>Xin chào, {user.username} <strong className={`role-badge ${user.role.toLowerCase()}`}>[{user.role}]</strong></span>
            <button type="button" className="logout-btn" onClick={handleLogout}>Đăng xuất</button>
          </>
        ) : <Link className="login-link" to="/login">Đăng nhập</Link>}
      </div>
    </nav>
  );
};

export default Navbar;
