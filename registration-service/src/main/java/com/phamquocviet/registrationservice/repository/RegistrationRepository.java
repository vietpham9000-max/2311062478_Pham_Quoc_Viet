package com.phamquocviet.registrationservice.repository;

import com.phamquocviet.registrationservice.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository
        extends JpaRepository<Registration, Long> {

    boolean existsByStudentIdAndCourseIdAndStatus(
            Long studentId,
            Long courseId,
            String status
    );

    List<Registration> findByStudentIdOrderByRegisteredAtDesc(Long studentId);
}
