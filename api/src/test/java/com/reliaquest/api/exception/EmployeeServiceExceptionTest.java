package com.reliaquest.api.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

class EmployeeServiceExceptionTest {

    @Test
    void testException_WithMessage() {
        String message = "Service unavailable";
        EmployeeServiceException ex = new EmployeeServiceException(message);

        assertThat(ex.getMessage()).isEqualTo(message);
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void testException_WithMessageAndCause() {
        String message = "Service failure";
        Throwable cause = new RuntimeException("Underlying issue");

        EmployeeServiceException ex = new EmployeeServiceException(message, cause);

        assertThat(ex.getMessage()).isEqualTo(message);
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    @Test
    void testResponseStatusAnnotation() {
        ResponseStatus status = EmployeeServiceException.class.getAnnotation(ResponseStatus.class);

        assertThat(status).isNotNull();
        assertThat(status.value()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
