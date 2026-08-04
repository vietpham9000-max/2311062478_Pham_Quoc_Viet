# API Blueprint

## Student Service

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | /students | Lấy danh sách sinh viên |
| GET | /students/{id} | Lấy sinh viên theo ID |
| POST | /students | Tạo sinh viên |
| PUT | /students/{id} | Cập nhật sinh viên |
| DELETE | /students/{id} | Xóa sinh viên |

## Course Service

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | /courses | Lấy danh sách khóa học |
| GET | /courses/{id} | Lấy khóa học theo ID |
| POST | /courses | Tạo khóa học |
| PUT | /courses/{id} | Cập nhật khóa học |
| DELETE | /courses/{id} | Xóa khóa học |

## Course Service Internal API

| Method | Endpoint | Chức năng |
|---|---|---|
| POST | /internal/courses/{id}/reserve-seat | Giữ một chỗ học |
| POST | /internal/courses/{id}/release-seat | Trả một chỗ học |

## Enrollment Service

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | /enrollments | Lấy danh sách đăng ký |
| GET | /enrollments/{id} | Lấy đăng ký theo ID |
| POST | /enrollments | Đăng ký khóa học |
| DELETE | /enrollments/{id} | Hủy đăng ký khóa học |
