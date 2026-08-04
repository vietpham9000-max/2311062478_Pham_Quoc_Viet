INSERT IGNORE INTO courses
(course_code, course_name, instructor, capacity, available_seats, price, status)
VALUES
('JAVA101', 'Lap trinh Java co ban', 'Nguyen Van An', 40, 35, 1500000, 'OPEN'),
('SPRING201', 'Phat trien Web voi Spring Boot', 'Tran Minh Tuan', 30, 18, 2200000, 'OPEN'),
('DB101', 'Co so du lieu MySQL', 'Le Thi Lan', 35, 0, 1300000, 'FULL'),
('MICRO301', 'Kien truc Microservices', 'Pham Quoc Minh', 25, 10, 2800000, 'OPEN'),
('DOCKER201', 'Docker danh cho lap trinh vien', 'Hoang Minh Duc', 20, 15, 1900000, 'OPEN');
