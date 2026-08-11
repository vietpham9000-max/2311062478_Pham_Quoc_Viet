package com.phamquocviet.registrationservice.service;

import com.phamquocviet.registrationservice.exception.CourseServiceUnavailableException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CourseClient {

    private final RestTemplate restTemplate;

    @Value("${course.service.base-url}")
    private String courseServiceBaseUrl;

    public CourseClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void reserveSeat(Long courseId) {
        callCourseService(courseId, "reserve-seat");
    }

    public void releaseSeat(Long courseId) {
        callCourseService(courseId, "release-seat");
    }

    private void callCourseService(
            Long courseId,
            String action
    ) {

        String url =
                courseServiceBaseUrl
                        + "/internal/courses/"
                        + courseId
                        + "/"
                        + action;

        try {

            restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.PATCH,
                    org.springframework.http.HttpEntity.EMPTY,
                    String.class
            );

        } catch (HttpClientErrorException exception) {

            int status =
                    exception.getStatusCode().value();

            if (status == 404) {
                throw new EntityNotFoundException(
                        "Không tìm thấy khóa học có id = "
                                + courseId
                );
            }

            if (status == 409) {

                if ("reserve-seat".equals(action)) {
                    throw new IllegalStateException(
                            "Khóa học đã hết chỗ, không thể đăng ký"
                    );
                }

                throw new IllegalStateException(
                        "Không thể trả lại chỗ cho khóa học"
                );
            }

            throw new IllegalArgumentException(
                    "course-service từ chối yêu cầu với HTTP "
                            + status
            );

        } catch (ResourceAccessException exception) {

            throw new CourseServiceUnavailableException(
                    "Không thể kết nối tới course-service",
                    exception
            );

        } catch (RestClientException exception) {

            throw new CourseServiceUnavailableException(
                    "Có lỗi khi gọi course-service",
                    exception
            );
        }
    }
}
