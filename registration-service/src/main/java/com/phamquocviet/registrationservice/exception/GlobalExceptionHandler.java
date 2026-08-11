package com.phamquocviet.registrationservice.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        Map<String, String> errors =
                new LinkedHashMap<>();

        exception
                .getBindingResult()
                .getFieldErrors()
                .forEach(
                        error -> errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> response =
                createErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Dữ liệu đầu vào không hợp lệ",
                        request.getRequestURI()
                );

        response.put("errors", errors);

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(
            EntityNotFoundException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleNotFound(
            EntityNotFoundException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        createErrorResponse(
                                HttpStatus.NOT_FOUND,
                                exception.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(
            IllegalStateException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleConflict(
            IllegalStateException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        createErrorResponse(
                                HttpStatus.CONFLICT,
                                exception.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleBadRequest(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .badRequest()
                .body(
                        createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                exception.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(
            CourseServiceUnavailableException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleCourseUnavailable(
            CourseServiceUnavailableException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(
                        HttpStatus.SERVICE_UNAVAILABLE
                )
                .body(
                        createErrorResponse(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                exception.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(
            HttpMessageNotReadableException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleInvalidJson(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .badRequest()
                .body(
                        createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "JSON không hợp lệ hoặc dữ liệu không đúng kiểu",
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>>
    handleGeneralException(
            Exception exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(
                        createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Đã xảy ra lỗi trong hệ thống",
                                request.getRequestURI()
                        )
                );
    }

    private Map<String, Object>
    createErrorResponse(
            HttpStatus status,
            String message,
            String path
    ) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                status.value()
        );

        response.put(
                "error",
                status.getReasonPhrase()
        );

        response.put(
                "message",
                message
        );

        response.put(
                "path",
                path
        );

        return response;
    }
}
