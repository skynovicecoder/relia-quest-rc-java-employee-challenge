package com.reliaquest.api.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleEmployeeServiceException() {
        String errorMessage = "Third party API down";
        EmployeeServiceException ex = new EmployeeServiceException(errorMessage);

        ProblemDetail problemDetail = handler.handleEmployeeServiceException(ex);

        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Employee Service Error");
        assertThat(problemDetail.getType())
                .isEqualTo(URI.create("urn:problem-type:server-for-employee-service-unavailable"));
        assertThat(problemDetail.getDetail()).contains("Employee Error Details").contains(errorMessage);
    }
}
