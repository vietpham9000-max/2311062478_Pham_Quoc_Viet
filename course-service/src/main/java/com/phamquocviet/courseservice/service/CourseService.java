package com.phamquocviet.courseservice.service;

import com.phamquocviet.courseservice.dto.CourseDTO;
import com.phamquocviet.courseservice.entity.Course;
import com.phamquocviet.courseservice.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /*
     * Lấy toàn bộ danh sách khóa học.
     */
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    /*
     * Lấy một khóa học theo ID.
     */
    public CourseDTO getCourseById(Long id) {
        Course course = findCourseById(id);
        return convertToDTO(course);
    }

    /*
     * Tạo khóa học mới.
     */
    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        String normalizedCourseCode =
                normalizeCourseCode(courseDTO.getCourseCode());

        if (courseRepository.existsByCourseCode(normalizedCourseCode)) {
            throw new IllegalStateException(
                    "Mã khóa học đã tồn tại: " + normalizedCourseCode
            );
        }

        validateAvailableSeats(courseDTO);

        Course course = new Course();

        updateCourseEntity(
                course,
                courseDTO,
                normalizedCourseCode
        );

        Course savedCourse = courseRepository.save(course);

        return convertToDTO(savedCourse);
    }

    /*
     * Cập nhật khóa học theo ID.
     */
    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course existingCourse = findCourseById(id);

        String normalizedCourseCode =
                normalizeCourseCode(courseDTO.getCourseCode());

        boolean courseCodeExists =
                courseRepository.existsByCourseCodeAndIdNot(
                        normalizedCourseCode,
                        id
                );

        if (courseCodeExists) {
            throw new IllegalStateException(
                    "Mã khóa học đã tồn tại: " + normalizedCourseCode
            );
        }

        validateAvailableSeats(courseDTO);

        updateCourseEntity(
                existingCourse,
                courseDTO,
                normalizedCourseCode
        );

        Course updatedCourse =
                courseRepository.save(existingCourse);

        return convertToDTO(updatedCourse);
    }

    /*
     * Xóa khóa học theo ID.
     */
    @Transactional
    public void deleteCourse(Long id) {
        Course course = findCourseById(id);
        courseRepository.delete(course);
    }

    /*
     * Tìm Entity Course theo ID.
     * Nếu không tồn tại thì phát sinh lỗi.
     */
    private Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy khóa học có ID: " + id
                ));
    }

    /*
     * Kiểm tra số chỗ còn lại không được lớn hơn sức chứa.
     */
    private void validateAvailableSeats(CourseDTO courseDTO) {
        Integer capacity = courseDTO.getCapacity();
        Integer availableSeats = courseDTO.getAvailableSeats();

        if (capacity != null
                && availableSeats != null
                && availableSeats > capacity) {

            throw new IllegalArgumentException(
                    "Số chỗ còn lại không được lớn hơn sức chứa"
            );
        }
    }

    /*
     * Chuẩn hóa mã khóa học:
     * - Xóa khoảng trắng đầu và cuối.
     * - Chuyển thành chữ in hoa.
     */
    private String normalizeCourseCode(String courseCode) {
        if (courseCode == null) {
            return null;
        }

        return courseCode
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    /*
     * Gán dữ liệu từ DTO sang Entity.
     */
    private void updateCourseEntity(
            Course course,
            CourseDTO courseDTO,
            String normalizedCourseCode
    ) {
        course.setCourseCode(normalizedCourseCode);
        course.setCourseName(trim(courseDTO.getCourseName()));
        course.setInstructor(trim(courseDTO.getInstructor()));
        course.setCapacity(courseDTO.getCapacity());
        course.setAvailableSeats(courseDTO.getAvailableSeats());
        course.setPrice(courseDTO.getPrice());
        course.setStatus(normalizeStatus(courseDTO.getStatus()));
    }

    /*
     * Chuyển Entity sang DTO để trả về API.
     */
    private CourseDTO convertToDTO(Course course) {
        CourseDTO courseDTO = new CourseDTO();

        courseDTO.setId(course.getId());
        courseDTO.setCourseCode(course.getCourseCode());
        courseDTO.setCourseName(course.getCourseName());
        courseDTO.setInstructor(course.getInstructor());
        courseDTO.setCapacity(course.getCapacity());
        courseDTO.setAvailableSeats(course.getAvailableSeats());
        courseDTO.setPrice(course.getPrice());
        courseDTO.setStatus(course.getStatus());

        return courseDTO;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }

        return value.trim();
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return null;
        }

        return status
                .trim()
                .toUpperCase(Locale.ROOT);
    }
}