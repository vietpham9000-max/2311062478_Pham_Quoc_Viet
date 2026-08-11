package com.phamquocviet.registrationservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RegistrationRequest {

    @NotNull(message = "studentId không được để trống")
    @Positive(message = "studentId phải lớn hơn 0")
    private Long studentId;

    @NotNull(message = "courseId không được để trống")
    @Positive(message = "courseId phải lớn hơn 0")
    private Long courseId;

    public RegistrationRequest() {
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
