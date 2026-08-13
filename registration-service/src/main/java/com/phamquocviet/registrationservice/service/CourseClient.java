package com.phamquocviet.registrationservice.service;

import com.phamquocviet.registrationservice.exception.CourseServiceUnavailableException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

            HttpHeaders headers = new HttpHeaders();
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (authorization != null) headers.set(HttpHeaders.AUTHORIZATION, authorization);
            }
            restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.PATCH,
                    new HttpEntity<>(headers),
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
