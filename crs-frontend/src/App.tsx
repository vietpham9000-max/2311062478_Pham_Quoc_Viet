import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import ProtectedRoute from "./components/ProtectedRoute";
import LoginPage from "./pages/LoginPage";
import CoursesPage from "./pages/CoursesPage";
import AdminCoursesPage from "./pages/admin/AdminCoursesPage";
import RegisterCoursePage from "./pages/RegisterCoursePage";
import MyRegistrationsPage from "./pages/MyRegistrationsPage";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<Navigate to="/courses" replace />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/courses" element={<CoursesPage />} />
        <Route path="/admin/courses" element={<ProtectedRoute requiredRole="ADMIN"><AdminCoursesPage /></ProtectedRoute>} />
        <Route path="/register-course" element={<ProtectedRoute requiredRole="STUDENT"><RegisterCoursePage /></ProtectedRoute>} />
        <Route path="/my-registrations" element={<ProtectedRoute requiredRole="STUDENT"><MyRegistrationsPage /></ProtectedRoute>} />
        <Route path="*" element={<Navigate to="/courses" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
