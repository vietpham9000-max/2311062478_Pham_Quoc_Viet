package com.phamquocviet.courseservice.controller;

import com.phamquocviet.courseservice.dto.CourseDTO;
import com.phamquocviet.courseservice.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /*
     * GET /courses
     * Lấy toàn bộ danh sách khóa học.
     */
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();

        return ResponseEntity.ok(courses);
    }

    /*
     * GET /courses/{id}
     * Lấy thông tin một khóa học theo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(
            @PathVariable Long id
    ) {
        CourseDTO course = courseService.getCourseById(id);

        return ResponseEntity.ok(course);
    }

    /*
     * POST /courses
     * Tạo một khóa học mới.
     */
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(
            @Valid @RequestBody CourseDTO courseDTO
    ) {
        CourseDTO createdCourse =
                courseService.createCourse(courseDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCourse);
    }

    /*
     * PUT /courses/{id}
     * Cập nhật toàn bộ thông tin khóa học.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO courseDTO
    ) {
        CourseDTO updatedCourse =
                courseService.updateCourse(id, courseDTO);

        return ResponseEntity.ok(updatedCourse);
    }

    /*
     * DELETE /courses/{id}
     * Xóa khóa học theo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id
    ) {
        courseService.deleteCourse(id);

        return ResponseEntity.noContent().build();
    }
}