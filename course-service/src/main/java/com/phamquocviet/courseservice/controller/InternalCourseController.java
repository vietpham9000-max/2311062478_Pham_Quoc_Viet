package com.phamquocviet.courseservice.controller;

import com.phamquocviet.courseservice.dto.CourseDTO;
import com.phamquocviet.courseservice.service.CourseService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/courses")
public class InternalCourseController {

    private final CourseService courseService;

    public InternalCourseController(
            CourseService courseService
    ) {
        this.courseService = courseService;
    }

    @PatchMapping("/{id}/reserve-seat")
    public CourseDTO reserveSeat(
            @PathVariable Long id
    ) {
        return courseService.reserveSeat(id);
    }

    @PatchMapping("/{id}/release-seat")
    public CourseDTO releaseSeat(
            @PathVariable Long id
    ) {
        return courseService.releaseSeat(id);
    }
}