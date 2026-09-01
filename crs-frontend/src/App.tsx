import { useState } from "react";
import CourseList from "./components/CourseList";
import SearchBox from "./components/SearchBox";
import Pagination from "./components/Pagination";
import CourseForm from "./components/CourseForm";
import { useCourses } from "./hooks/useCourses";
import { deleteCourse } from "./api/courseApi";
import { getApiErrorMessage } from "./types/apiError";
import type { Course } from "./types/course";
import "./App.css";

const PAGE_SIZE = 3;

function App() {
  const [keyword, setKeyword] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [editingCourse, setEditingCourse] = useState<Course | null>(null);
  const [message, setMessage] = useState("");
  const [actionError, setActionError] = useState("");

  const { courses, totalPages, state, errorMessage, refetch } = useCourses(
    keyword,
    currentPage,
    PAGE_SIZE
  );

  const handleSearch = (newKeyword: string) => {
    setKeyword(newKeyword);
    setCurrentPage(0);
  };

  const handleFormSuccess = async (successMessage: string) => {
    setMessage(successMessage);
    setActionError("");
    setEditingCourse(null);
    await refetch();
  };

  const handleDelete = async (course: Course) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa ${course.courseCode} - ${course.courseName}?`
    );
    if (!confirmed) return;

    setMessage("");
    setActionError("");
    try {
      await deleteCourse(course.id);
      setMessage("Xóa khóa học thành công.");
      if (editingCourse?.id === course.id) setEditingCourse(null);
      if (courses.length === 1 && currentPage > 0) {
        setCurrentPage((page) => page - 1);
      } else {
        await refetch();
      }
    } catch (error: unknown) {
      setActionError(getApiErrorMessage(error));
    }
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <h1>Danh sách môn học</h1>
        <p>Tìm kiếm và quản lý danh sách môn học</p>
      </header>

      <main className="app-content">
        {message && <div className="alert success-alert">{message}</div>}
        {actionError && <div className="alert error-alert">{actionError}</div>}

        <CourseForm
          editingCourse={editingCourse}
          onSuccess={handleFormSuccess}
          onCancelEdit={() => setEditingCourse(null)}
        />

        <SearchBox value={keyword} onChange={handleSearch} />

        <div className="card">
          <CourseList
            courses={courses}
            state={state}
            errorMessage={errorMessage}
            refetch={refetch}
            onEdit={(course) => {
              setEditingCourse(course);
              setMessage("");
              setActionError("");
              window.scrollTo({ top: 0, behavior: "smooth" });
            }}
            onDelete={handleDelete}
          />
        </div>

        {state === "success" && (
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={setCurrentPage}
          />
        )}
      </main>
    </div>
  );
}

export default App;
