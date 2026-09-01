package com.phamquocviet.courseservice.service;

import com.phamquocviet.courseservice.dto.CourseDTO;
import com.phamquocviet.courseservice.entity.Course;
import com.phamquocviet.courseservice.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Tìm kiếm khóa học theo tên hoặc mã, có phân trang/sắp xếp.
     * Nếu keyword rỗng thì trả về toàn bộ dữ liệu theo Pageable.
     */
    public Page<CourseDTO> search(String keyword, Pageable pageable) {

        Page<Course> page = (keyword == null || keyword.isBlank())
                ? courseRepository.findAll(pageable)
                : courseRepository
                        .findByCourseNameContainingIgnoreCaseOrCourseCodeContainingIgnoreCase(
                                keyword.trim(),
                                keyword.trim(),
                                pageable);

        return page.map(this::convertToDTO);
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
        String normalizedCourseCode = normalizeCourseCode(courseDTO.getCourseCode());
        String normalizedCourseName = trim(courseDTO.getCourseName());

        if (courseRepository.existsByCourseCode(normalizedCourseCode)) {
            throw new IllegalStateException(
                    "Mã khóa học đã tồn tại: " + normalizedCourseCode);
        }

        if (courseRepository.existsByCourseNameIgnoreCase(normalizedCourseName)) {
            throw new IllegalStateException(
                    "Tên môn học đã tồn tại: " + normalizedCourseName);
        }

        validateAvailableSeats(courseDTO);

        Course course = new Course();

        updateCourseEntity(
                course,
                courseDTO,
                normalizedCourseCode,
                normalizedCourseName);

        Course savedCourse = courseRepository.save(course);

        return convertToDTO(savedCourse);
    }

    /*
     * Cập nhật khóa học theo ID.
     */
    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course existingCourse = findCourseById(id);

        String normalizedCourseCode = normalizeCourseCode(courseDTO.getCourseCode());
        String normalizedCourseName = trim(courseDTO.getCourseName());

        boolean courseCodeExists = courseRepository.existsByCourseCodeAndIdNot(
                normalizedCourseCode,
                id);

        if (courseCodeExists) {
            throw new IllegalStateException(
                    "Mã khóa học đã tồn tại: " + normalizedCourseCode);
        }


        boolean courseNameExists = courseRepository.existsByCourseNameIgnoreCaseAndIdNot(
                normalizedCourseName,
                id);

        if (courseNameExists) {
            throw new IllegalStateException(
                    "Tên môn học đã tồn tại: " + normalizedCourseName);
        }

        validateAvailableSeats(courseDTO);

        updateCourseEntity(
                existingCourse,
                courseDTO,
                normalizedCourseCode,
                normalizedCourseName);

        Course updatedCourse = courseRepository.save(existingCourse);

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
                        "Không tìm thấy khóa học có ID: " + id));
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
                    "Số chỗ còn lại không được lớn hơn sức chứa");
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
            String normalizedCourseCode,
            String normalizedCourseName) {
        course.setCourseCode(normalizedCourseCode);
        course.setCourseName(normalizedCourseName);
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

    /*
     * API nội bộ: giữ một chỗ học.
     */
    @Transactional
    public CourseDTO reserveSeat(Long courseId) {

        Course course = findCourseById(courseId);

        if (course.getAvailableSeats() == null
                || course.getAvailableSeats() <= 0) {

            throw new IllegalStateException(
                    "Khóa học đã hết chỗ, không thể đăng ký");
        }

        course.setAvailableSeats(
                course.getAvailableSeats() - 1);

        return convertToDTO(
                courseRepository.save(course));
    }

    /*
     * API nội bộ: trả lại một chỗ học.
     * Không cho availableSeats vượt capacity.
     */
    @Transactional
    public CourseDTO releaseSeat(Long courseId) {

        Course course = findCourseById(courseId);

        if (course.getAvailableSeats() < course.getCapacity()) {
            course.setAvailableSeats(
                    course.getAvailableSeats() + 1);
        }

        return convertToDTO(
                courseRepository.save(course));
    }
}
