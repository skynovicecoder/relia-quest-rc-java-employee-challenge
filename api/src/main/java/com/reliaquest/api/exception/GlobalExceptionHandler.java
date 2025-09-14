package com.reliaquest.api.exception;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeServiceException.class)
    public ProblemDetail handleEmployeeServiceException(EmployeeServiceException ex) {
        String details = ex.getMessage();
        log.error("Logging EmployeeServiceException : {}", details);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        pd.setType(URI.create("urn:problem-type:server-for-employee-service-unavailable"));
        pd.setDetail("Employee Error Details : " + details);
        pd.setTitle("Employee Service Error");

        return pd;
    }

    /*@ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error: " + ex.getMessage());
    }*/
}
