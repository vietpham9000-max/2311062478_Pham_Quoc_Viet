package com.phamquocviet.registrationservice.service;

import com.phamquocviet.registrationservice.dto.RegistrationRequest;
import com.phamquocviet.registrationservice.entity.Registration;
import com.phamquocviet.registrationservice.repository.RegistrationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrationService {

    private static final String STATUS_CONFIRMED =
            "CONFIRMED";

    private static final String STATUS_CANCELLED =
            "CANCELLED";

    private final RegistrationRepository registrationRepository;
    private final CourseClient courseClient;

    public RegistrationService(
            RegistrationRepository registrationRepository,
            CourseClient courseClient
    ) {
        this.registrationRepository =
                registrationRepository;

        this.courseClient =
                courseClient;
    }

    @Transactional
    public Registration register(
            RegistrationRequest request
    ) {

        boolean duplicated =
                registrationRepository
                        .existsByStudentIdAndCourseIdAndStatus(
                                request.getStudentId(),
                                request.getCourseId(),
                                STATUS_CONFIRMED
                        );

        if (duplicated) {
            throw new IllegalStateException(
                    "Sinh viên đã đăng ký môn học này"
            );
        }

        /*
         * Giữ chỗ bên course-service trước.
         */
        courseClient.reserveSeat(
                request.getCourseId()
        );

        try {

            Registration registration =
                    new Registration();

            registration.setStudentId(
                    request.getStudentId()
            );

            registration.setCourseId(
                    request.getCourseId()
            );

            registration.setStatus(
                    STATUS_CONFIRMED
            );

            registration.setRegisteredAt(
                    LocalDateTime.now()
            );

            /*
             * saveAndFlush để lỗi DB xảy ra ngay tại đây,
             * giúp compensation có thể chạy.
             */
            return registrationRepository
                    .saveAndFlush(registration);

        } catch (RuntimeException exception) {

            /*
             * Compensation:
             * nếu DB registration lỗi sau khi đã giữ chỗ,
             * trả lại chỗ cho course-service.
             */
            try {
                courseClient.releaseSeat(
                        request.getCourseId()
                );
            } catch (RuntimeException ignored) {
            }

            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public Registration getById(Long id) {

        return registrationRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Không tìm thấy đăng ký có id = "
                                        + id
                        )
                );
    }

    @Transactional
    public Registration cancel(Long id) {

        Registration registration =
                registrationRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException(
                                        "Không tìm thấy đăng ký có id = "
                                                + id
                                )
                        );

        if (STATUS_CANCELLED.equals(
                registration.getStatus()
        )) {
            throw new IllegalStateException(
                    "Đăng ký này đã được hủy trước đó"
            );
        }

        /*
         * Trả chỗ bên course-service.
         */
        courseClient.releaseSeat(
                registration.getCourseId()
        );

        try {

            registration.setStatus(
                    STATUS_CANCELLED
            );

            return registrationRepository
                    .saveAndFlush(registration);

        } catch (RuntimeException exception) {

            /*
             * Compensation ngược:
             * DB không cập nhật CANCELLED được
             * thì cố giữ lại chỗ.
             */
            try {
                courseClient.reserveSeat(
                        registration.getCourseId()
                );
            } catch (RuntimeException ignored) {
            }

            throw exception;
        }
    }
}
