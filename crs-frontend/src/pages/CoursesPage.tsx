import { useState } from "react";
import CourseList from "../components/CourseList";
import SearchBox from "../components/SearchBox";
import Pagination from "../components/Pagination";
import { useCourses } from "../hooks/useCourses";

const PAGE_SIZE = 3;

const CoursesPage = () => {
  const [keyword, setKeyword] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const { courses, totalPages, state, errorMessage, refetch } = useCourses(keyword, currentPage, PAGE_SIZE);

  const handleSearch = (newKeyword: string) => {
    setKeyword(newKeyword);
    setCurrentPage(0);
  };

  return (
    <div className="app-container">
      <header className="app-header"><h1>Danh sách môn học</h1><p>Tìm kiếm và xem các môn học hiện có</p></header>
      <main className="app-content">
        <SearchBox value={keyword} onChange={handleSearch} />
        <div className="card"><CourseList courses={courses} state={state} errorMessage={errorMessage} refetch={refetch} /></div>
        {state === "success" && <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} />}
      </main>
    </div>
  );
};

export default CoursesPage;
