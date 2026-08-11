package com.phamquocviet.courseservice.repository;

import com.phamquocviet.courseservice.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByCourseCode(String courseCode);

    boolean existsByCourseCodeAndIdNot(String courseCode, Long id);

    Page<Course> findByCourseNameContainingIgnoreCaseOrCourseCodeContainingIgnoreCase(
            String courseName,
            String courseCode,
            Pageable pageable
    );
}