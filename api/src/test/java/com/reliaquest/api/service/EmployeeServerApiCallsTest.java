package com.reliaquest.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.reliaquest.api.dto.*;
import com.reliaquest.api.util.EmployeeTestDataGenerator;
import java.net.ConnectException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

class EmployeeServerApiCallsTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @InjectMocks
    private EmployeeServerApiCalls apiCalls;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private <T> void mockGetResponse(String uri, T response) {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(uri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body((Class<T>) response.getClass())).thenReturn(response);
    }

    private <T> void mockGetResponseWithPath(String uriTemplate, Object uriVar, T response) {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(uriTemplate, uriVar)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body((Class<T>) response.getClass())).thenReturn(response);
    }

    private <T> void mockPostResponse(Object bodyInput, T response) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(bodyInput)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body((Class<T>) response.getClass())).thenReturn(response);
    }

    private <T> void mockDeleteResponse(Map<String, String> bodyInput, T response) {
        when(restClient.method(HttpMethod.DELETE)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(bodyInput)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body((Class<T>) response.getClass())).thenReturn(response);
    }

    private <T> void mockException(Runnable fluentApiCall, ResourceAccessException ex) {
        assertThatThrownBy(fluentApiCall::run)
                .isInstanceOf(ResourceAccessException.class)
                .hasMessageContaining(ex.getMessage());
    }

    @Test
    void testGetAllEmployees() {
        EmployeeResponse expected = new EmployeeResponse(EmployeeTestDataGenerator.generateEmployees(3), "OK");
        mockGetResponse("", expected);

        assertThat(apiCalls.getAllEmployeesApiCall()).isEqualTo(expected);
    }

    @Test
    void testGetAllEmployeesApiCall_connectionFailure() {
        ResourceAccessException ex =
                new ResourceAccessException("Connection error", new ConnectException("Connection refused"));

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(EmployeeResponse.class)).thenThrow(ex);

        assertThatThrownBy(() -> apiCalls.getAllEmployeesApiCall())
                .isInstanceOf(ResourceAccessException.class)
                .hasMessageContaining("Connection error");
    }

    @Test
    void testGetAllEmployeesAfterServerConnectionError() {
        EmployeeResponse expected = new EmployeeResponse(EmployeeTestDataGenerator.generateEmployees(3), "OK");
        mockGetResponse("", expected);

        assertThat(apiCalls.getAllEmployeesApiCallAfterServerConnectionError()).isEqualTo(expected);
    }

    @Test
    void testGetEmployeeByID_success() {
        String id = "123";
        Employee employee = EmployeeTestDataGenerator.generateEmployees(1).get(0);
        employee.setId(id);
        SoloEmployeeResponse expected = new SoloEmployeeResponse(employee, "OK");
        mockGetResponseWithPath("/{id}", id, expected);

        assertThat(apiCalls.getEmployeeByID(id)).isEqualTo(expected);
    }

    @Test
    void testGetEmployeeByID_connectionFailure() {
        String id = "123";
        ResourceAccessException ex =
                new ResourceAccessException("Connection error", new ConnectException("Connection refused"));
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/{id}", id)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SoloEmployeeResponse.class)).thenThrow(ex);

        mockException(() -> apiCalls.getEmployeeByID(id), ex);
    }

    @Test
    void testGetEmployeeByID_directConnectionFailure() {
        String id = "123";
        ResourceAccessException ex =
                new ResourceAccessException("Connection error", new ConnectException("Connection refused"));
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/{id}", id)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SoloEmployeeResponse.class)).thenThrow(ex);

        mockException(() -> apiCalls.getEmployeeByIDAfterServerConnectionError(id), ex);
    }

    @Test
    void testCreateEmployeeApiCall_success() {
        EmployeeInput input = new EmployeeInput();
        input.setName("Alice");
        input.setSalary(50000);

        Employee employee = EmployeeTestDataGenerator.generateEmployees(1).get(0);
        employee.setId("101");
        SoloEmployeeResponse expected = new SoloEmployeeResponse(employee, "CREATED");

        mockPostResponse(input, expected);

        assertThat(apiCalls.createEmployeeApiCall(input)).isEqualTo(expected);
    }

    @Test
    void testCreateEmployeeApiCall_connectionFailure() {
        EmployeeInput input = new EmployeeInput();
        input.setName("Alice");
        input.setSalary(50000);

        ResourceAccessException ex =
                new ResourceAccessException("Connection error", new ConnectException("Connection refused"));
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(input)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SoloEmployeeResponse.class)).thenThrow(ex);

        mockException(() -> apiCalls.createEmployeeApiCall(input), ex);
    }

    @Test
    void testCreateEmployeeApiCall_directConnectionFailure() {
        EmployeeInput input = new EmployeeInput();
        input.setName("Alice");
        input.setSalary(50000);

        ResourceAccessException ex =
                new ResourceAccessException("Connection error", new ConnectException("Connection refused"));
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(input)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SoloEmployeeResponse.class)).thenThrow(ex);

        mockException(() -> apiCalls.createEmployeeApiCallAfterServerConnectionError(input), ex);
    }

    @Test
    void testDeleteEmployeeApiCall_success() {
        Map<String, String> requestBody = Map.of("name", "test_user");
        DeleteEmployeeResponse expected = new DeleteEmployeeResponse(true, "DELETED");

        mockDeleteResponse(requestBody, expected);

        assertThat(apiCalls.deleteEmployeeApiCall(requestBody)).isEqualTo(expected);
    }

    @Test
    void testDeleteEmployeeApiCall_connectionFailure() {
        Map<String, String> requestBody = Map.of("name", "test_user");
        ResourceAccessException ex =
                new ResourceAccessException("Connection error", new ConnectException("Connection refused"));

        when(restClient.method(HttpMethod.DELETE)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(requestBody)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(DeleteEmployeeResponse.class)).thenThrow(ex);

        mockException(() -> apiCalls.deleteEmployeeApiCall(requestBody), ex);
    }

    @Test
    void testDeleteEmployeeApiCall_directConnectionFailure() {
        Map<String, String> requestBody = Map.of("name", "test_user");
        ResourceAccessException ex =
                new ResourceAccessException("Connection error", new ConnectException("Connection refused"));

        when(restClient.method(HttpMethod.DELETE)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(requestBody)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(DeleteEmployeeResponse.class)).thenThrow(ex);

        mockException(() -> apiCalls.deleteEmployeeApiCallAfterServerConnectionError(requestBody), ex);
    }
}
