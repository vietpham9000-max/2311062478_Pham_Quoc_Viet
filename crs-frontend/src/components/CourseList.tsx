import { useEffect, useState } from "react";
import { getCourses } from "../api/courseApi";
import type { Course } from "../types/course";

function CourseList() {
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        const data = await getCourses();

        setCourses(data.content);
      } catch (err) {
        console.error(err);
        setError("Không thể tải danh sách khóa học");
      } finally {
        setLoading(false);
      }
    };

    fetchCourses();
  }, []);

  if (loading) {
    return <p>Đang tải danh sách khóa học...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <div>
      <h2>Danh sách khóa học</h2>

      {courses.length === 0 ? (
        <p>Chưa có khóa học nào.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Mã</th>
              <th>Tên khóa học</th>
              <th>Giảng viên</th>
              <th>Sức chứa</th>
              <th>Còn chỗ</th>
              <th>Học phí</th>
              <th>Trạng thái</th>
            </tr>
          </thead>

          <tbody>
            {courses.map((course) => (
              <tr key={course.id}>
                <td>{course.courseCode}</td>
                <td>{course.courseName}</td>
                <td>{course.instructor}</td>
                <td>{course.capacity}</td>
                <td>{course.availableSeats}</td>
                <td>{course.price}</td>
                <td>{course.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default CourseList;