import type { Course } from "../types/course";
import type { UseCoursesState } from "../hooks/useCourses";

interface CourseListProps {
  courses: Course[];
  state: UseCoursesState;
  errorMessage: string;
  refetch: () => void;
  onEdit: (course: Course) => void;
  onDelete: (course: Course) => void;
}

function CourseList({ courses, state, errorMessage, refetch, onEdit, onDelete }: CourseListProps) {
  if (state === "loading") {
    return (
      <div className="state-message">
        <div className="spinner"></div>
        <p>Đang tải danh sách môn học...</p>
        <p className="sub-message">Vui lòng chờ trong giây lát.</p>
      </div>
    );
  }

  if (state === "error") {
    return (
      <div className="state-message error">
        <p>Không thể tải dữ liệu</p>
        {errorMessage && <p className="error-detail">{errorMessage}</p>}
        <button onClick={refetch} className="retry-btn">
          Thử lại
        </button>
      </div>
    );
  }

  if (state === "empty") {
    return (
      <div className="state-message">
        <p>Không tìm thấy môn học phù hợp.</p>
        <p className="sub-message">Hãy thử một từ khóa khác.</p>
      </div>
    );
  }

  const getStatusText = (status: string) => {
    switch (status) {
      case "OPEN":
        return "Đang mở";
      case "FULL":
        return "Đã đầy";
      case "CLOSED":
        return "Đã đóng";
      default:
        return status;
    }
  };

  return (
    <div className="table-container">
      <table>
        <thead>
          <tr>
            <th>STT</th>
            <th>Mã môn học</th>
            <th>Tên môn học</th>
            <th>Giảng viên</th>
            <th>Sức chứa</th>
            <th>Còn chỗ</th>
            <th>Học phí</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {courses.map((course, index) => (
            <tr key={course.id}>
              <td>{index + 1}</td>
              <td className="font-semibold">{course.courseCode}</td>
              <td>{course.courseName}</td>
              <td>{course.instructor}</td>
              <td>{course.capacity}</td>
              <td>{course.availableSeats}</td>
              <td>
                {new Intl.NumberFormat("vi-VN", {
                  style: "currency",
                  currency: "VND",
                }).format(course.price)}
              </td>
              <td>
                <span className={`status-badge ${course.status.toLowerCase()}`}>
                  {getStatusText(course.status)}
                </span>
              </td>
              <td>
                <div className="row-actions">
                  <button className="edit-btn" type="button" onClick={() => onEdit(course)}>Sửa</button>
                  <button className="delete-btn" type="button" onClick={() => onDelete(course)}>Xóa</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default CourseList;
