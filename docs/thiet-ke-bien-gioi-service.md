# Thiết kế biên giới Service

## 1. API Gateway

- Cổng dự kiến: 8080
- Tiếp nhận request từ client.
- Định tuyến request đến các service.
- Không sở hữu database riêng.

## 2. Student Service

- Cổng dự kiến: 8081
- Database riêng: student_db
- Quản lý thông tin sinh viên.
- Không truy cập trực tiếp database của service khác.

## 3. Course Service

- Cổng: 8082
- Database riêng: course_db
- Quản lý thông tin khóa học.
- Quản lý sức chứa và số chỗ còn lại.
- Cung cấp API nội bộ reserve-seat và release-seat.

## 4. Enrollment Service

- Cổng dự kiến: 8083
- Database riêng: enrollment_db
- Quản lý việc sinh viên đăng ký khóa học.
- Gọi API nội bộ của course-service để giữ hoặc trả chỗ.

## Bảng định tuyến API Gateway dự kiến

| Đường dẫn | Service đích | Cổng |
|---|---|---|
| /students/** | student-service | 8081 |
| /courses/** | course-service | 8082 |
| /enrollments/** | enrollment-service | 8083 |

## Nguyên tắc database riêng

- student-service chỉ truy cập student_db.
- course-service chỉ truy cập course_db.
- enrollment-service chỉ truy cập enrollment_db.
- Các service trao đổi dữ liệu thông qua REST API.
