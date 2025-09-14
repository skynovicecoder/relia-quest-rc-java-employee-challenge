package com.reliaquest.api.service;

import com.reliaquest.api.dto.DeleteEmployeeResponse;
import com.reliaquest.api.dto.EmployeeInput;
import com.reliaquest.api.dto.EmployeeResponse;
import com.reliaquest.api.dto.SoloEmployeeResponse;
import com.reliaquest.api.exception.EmployeeServiceException;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.ConnectException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class EmployeeServerApiCalls {
    private final RestClient restClient;

    @Autowired
    public EmployeeServerApiCalls(RestClient employeeRestClient) {
        this.restClient = employeeRestClient;
    }

    @Retry(name = "employeeServerRetryForTooManyReq")
    public EmployeeResponse getAllEmployeesApiCall() throws ResourceAccessException {
        log.debug("EmployeeServerApiCalls : inside getAllEmployeesApiCall");
        try {
            return restClient.get().uri("").retrieve().body(EmployeeResponse.class);
        } catch (ResourceAccessException ex) {
            log.error("Connection error: {}", ex.getMessage());
            if (ex.getCause() instanceof ConnectException) {
                log.error("Root cause is ConnectException: {}", ex.getCause().getMessage());
            }
            throw ex;
        }
    }

    @Retry(name = "employeeServerConnectionRetry", fallbackMethod = "fallbackGetAllEmployeesApiServerCall")
    public EmployeeResponse getAllEmployeesApiCallAfterServerConnectionError() {
        log.debug("EmployeeServerApiCalls : inside getAllEmployeesApiCallAfterServerConnectionError");
        return restClient.get().uri("").retrieve().body(EmployeeResponse.class);
    }

    public EmployeeResponse fallbackGetAllEmployeesApiServerCall(Throwable throwable) {
        log.warn("EmployeeServerApiCalls : inside fallbackGetAllEmployeesApiServerCall");
        throw new EmployeeServiceException("Failed to fetch employees after retries", throwable);
    }

    @Retry(name = "employeeServerRetryForTooManyReq")
    public SoloEmployeeResponse getEmployeeByID(String id) throws ResourceAccessException {
        log.debug("EmployeeServerApiCalls : inside getEmployeeByID");
        try {
            return restClient.get().uri("/{id}", id).retrieve().body(SoloEmployeeResponse.class);
        } catch (ResourceAccessException ex) {
            log.error("Connection error while trying getEmployeeByID: {}", ex.getMessage());
            if (ex.getCause() instanceof ConnectException) {
                log.error(
                        "While getEmployeeByID root cause is ConnectException: {}",
                        ex.getCause().getMessage());
            }
            throw ex;
        }
    }

    @Retry(name = "employeeServerConnectionRetry", fallbackMethod = "fallbackGetEmployeeByIDServerCall")
    public SoloEmployeeResponse getEmployeeByIDAfterServerConnectionError(String id) {
        log.debug("EmployeeServerApiCalls : inside getEmployeeByIDAfterServerConnectionError");
        return restClient.get().uri("/{id}", id).retrieve().body(SoloEmployeeResponse.class);
    }

    public SoloEmployeeResponse fallbackGetEmployeeByIDServerCall(String id, Throwable throwable) {
        log.warn("EmployeeServerApiCalls : inside fallbackGetEmployeeByIDServerCall");
        throw new EmployeeServiceException(
                "Failed to fetch employee details for id " + id + " after retries", throwable);
    }

    @Retry(name = "employeeServerRetryForTooManyReq")
    public SoloEmployeeResponse createEmployeeApiCall(EmployeeInput employeeInput) throws ResourceAccessException {
        log.debug("EmployeeServerApiCalls : inside createEmployeeApiCall");
        try {
            return restClient.post().uri("").body(employeeInput).retrieve().body(SoloEmployeeResponse.class);
        } catch (ResourceAccessException ex) {
            log.error("Connection error while trying createEmployeeApiCall: {}", ex.getMessage());
            if (ex.getCause() instanceof ConnectException) {
                log.error(
                        "While createEmployeeApiCall root cause is ConnectException: {}",
                        ex.getCause().getMessage());
            }
            throw ex;
        }
    }

    @Retry(name = "employeeServerConnectionRetry", fallbackMethod = "fallbackCreateEmployeeApiCall")
    public SoloEmployeeResponse createEmployeeApiCallAfterServerConnectionError(EmployeeInput employeeInput) {
        log.debug("EmployeeServerApiCalls : inside createEmployeeApiCallAfterServerConnectionError");
        return restClient.post().uri("").body(employeeInput).retrieve().body(SoloEmployeeResponse.class);
    }

    public SoloEmployeeResponse fallbackCreateEmployeeApiCall(EmployeeInput employeeInput, Throwable throwable) {
        log.warn("EmployeeServerApiCalls : inside fallbackCreateEmployeeApiCall");
        throw new EmployeeServiceException(
                "Failed to create employee data for name " + employeeInput.getName() + " after retries", throwable);
    }

    @Retry(name = "employeeServerRetryForTooManyReq")
    public DeleteEmployeeResponse deleteEmployeeApiCall(Map<String, String> requestBody)
            throws ResourceAccessException {
        log.debug("EmployeeServerApiCalls : inside deleteEmployeeApiCall");
        try {
            return restClient
                    .method(HttpMethod.DELETE)
                    .uri("")
                    .body(requestBody)
                    .retrieve()
                    .body(DeleteEmployeeResponse.class);
        } catch (ResourceAccessException ex) {
            log.error("Connection error while trying deleteEmployeeApiCall: {}", ex.getMessage());
            if (ex.getCause() instanceof ConnectException) {
                log.error(
                        "While deleteEmployeeApiCall root cause is ConnectException: {}",
                        ex.getCause().getMessage());
            }
            throw ex;
        }
    }

    @Retry(name = "employeeServerConnectionRetry", fallbackMethod = "fallbackDeleteEmployeeApiCall")
    public DeleteEmployeeResponse deleteEmployeeApiCallAfterServerConnectionError(Map<String, String> requestBody) {
        log.debug("EmployeeServerApiCalls : inside deleteEmployeeApiCallAfterServerConnectionError");
        return restClient
                .method(HttpMethod.DELETE)
                .uri("")
                .body(requestBody)
                .retrieve()
                .body(DeleteEmployeeResponse.class);
    }

    public DeleteEmployeeResponse fallbackDeleteEmployeeApiCall(Map<String, String> requestBody, Throwable throwable) {
        log.warn("EmployeeServerApiCalls : inside fallbackDeleteEmployeeApiCall");
        throw new EmployeeServiceException(
                "Failed to delete employee data for name " + requestBody.get("name") + " after retries", throwable);
    }
}
