package com.phamquocviet.registrationservice.exception;

public class CourseServiceUnavailableException
        extends RuntimeException {

    public CourseServiceUnavailableException(String message) {
        super(message);
    }

    public CourseServiceUnavailableException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
