import { useState } from "react";
import CourseList from "../components/CourseList";
import SearchBox from "../components/SearchBox";
import Pagination from "../components/Pagination";
import Toast from "../components/Toast";
import { useCourses } from "../hooks/useCourses";
import { useToast } from "../hooks/useToast";
import { useAuth } from "../context/AuthContext";
import { registerCourse } from "../api/registrationApi";
import { getApiErrorMessage } from "../types/apiError";
import type { Course } from "../types/course";

const PAGE_SIZE = 3;

const RegisterCoursePage = () => {
  const [keyword, setKeyword] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [registeringId, setRegisteringId] = useState<number | null>(null);
  const { user } = useAuth();
  const { toast, showToast, clearToast } = useToast();
  const { courses, totalPages, state, errorMessage, refetch } = useCourses(keyword, currentPage, PAGE_SIZE);

  const handleRegister = async (course: Course) => {
    if (!user) return;
    setRegisteringId(course.id);
    try {
      await registerCourse({ studentId: user.userId, courseId: course.id });
      showToast(`Đăng ký thành công môn ${course.courseName}.`, "success");
      await refetch();
    } catch (error: unknown) {
      showToast(getApiErrorMessage(error), "error");
    } finally {
      setRegisteringId(null);
    }
  };

  const handleSearch = (newKeyword: string) => {
    setKeyword(newKeyword);
    setCurrentPage(0);
  };

  return (
    <div className="app-container">
      <header className="app-header"><h1>Đăng ký khóa học</h1><p>Chọn môn học đang mở và còn chỗ để đăng ký</p></header>
      <main className="app-content">
        <SearchBox value={keyword} onChange={handleSearch} />
        <div className="card">
          <CourseList courses={courses} state={state} errorMessage={errorMessage} refetch={refetch} onRegister={handleRegister} registeringId={registeringId} />
        </div>
        {state === "success" && <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} />}
      </main>
      {toast && <Toast message={toast.message} type={toast.type} onClose={clearToast} />}
    </div>
  );
};

export default RegisterCoursePage;
