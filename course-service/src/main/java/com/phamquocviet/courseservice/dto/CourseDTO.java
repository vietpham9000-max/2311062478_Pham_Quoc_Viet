package com.phamquocviet.courseservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class CourseDTO {

    private Long id;

    @NotBlank(message = "Mã khóa học không được để trống")
    @Size(max = 20, message = "Mã khóa học không được vượt quá 20 ký tự")
    private String courseCode;

    @NotBlank(message = "Tên khóa học không được để trống")
    @Size(max = 150, message = "Tên khóa học không được vượt quá 150 ký tự")
    private String courseName;

    @NotBlank(message = "Tên giảng viên không được để trống")
    @Size(max = 100, message = "Tên giảng viên không được vượt quá 100 ký tự")
    private String instructor;

    @NotNull(message = "Sức chứa không được để trống")
    @Min(value = 1, message = "Sức chứa phải lớn hơn hoặc bằng 1")
    private Integer capacity;

    @NotNull(message = "Số chỗ còn lại không được để trống")
    @Min(value = 0, message = "Số chỗ còn lại không được nhỏ hơn 0")
    private Integer availableSeats;

    @NotNull(message = "Học phí không được để trống")
    @PositiveOrZero(message = "Học phí không được nhỏ hơn 0")
    private Double price;

    @NotBlank(message = "Trạng thái không được để trống")
    @Pattern(
            regexp = "OPEN|FULL|CLOSED",
            message = "Trạng thái chỉ được là OPEN, FULL hoặc CLOSED"
    )
    private String status;

    public CourseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}