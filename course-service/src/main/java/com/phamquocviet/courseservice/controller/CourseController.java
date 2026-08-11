package com.phamquocviet.courseservice.controller;

import com.phamquocviet.courseservice.dto.CourseDTO;
import com.phamquocviet.courseservice.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /*
     * GET /courses
     * Hỗ trợ:
     * - keyword: tìm theo tên hoặc mã khóa học
     * - page, size: phân trang
     * - sort: sắp xếp
     */
    @GetMapping
    public Page<CourseDTO> search(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        return courseService.search(keyword, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                courseService.getCourseById(id)
        );
    }

    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(
            @Valid @RequestBody CourseDTO courseDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courseService.createCourse(courseDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO courseDTO
    ) {
        return ResponseEntity.ok(
                courseService.updateCourse(id, courseDTO)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id
    ) {
        courseService.deleteCourse(id);

        return ResponseEntity.noContent().build();
    }
}