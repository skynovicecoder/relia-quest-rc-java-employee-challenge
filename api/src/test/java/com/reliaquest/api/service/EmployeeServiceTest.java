package com.reliaquest.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.reliaquest.api.dto.*;
import com.reliaquest.api.exception.EmployeeServiceException;
import com.reliaquest.api.util.EmployeeTestDataGenerator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;

class EmployeeServiceTest {

    private EmployeeServerApiCalls apiCalls;
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        apiCalls = mock(EmployeeServerApiCalls.class);
        employeeService = new EmployeeService(apiCalls);
    }

    @Test
    void getAllEmployees_shouldReturnList_whenApiCallSucceeds() {
        List<Employee> employees = EmployeeTestDataGenerator.generateEmployees(5);
        EmployeeResponse response = new EmployeeResponse(employees, "OK");
        when(apiCalls.getAllEmployeesApiCall()).thenReturn(response);

        List<Employee> result = employeeService.getAllEmployees();
        assertThat(result).hasSize(5).containsAll(employees);
    }

    @Test
    void getAllEmployees_shouldFallback_whenResourceAccessException() {
        List<Employee> fallbackEmployees = EmployeeTestDataGenerator.generateEmployees(3);
        EmployeeResponse fallback = new EmployeeResponse(fallbackEmployees, "OK");

        when(apiCalls.getAllEmployeesApiCall()).thenThrow(ResourceAccessException.class);
        when(apiCalls.getAllEmployeesApiCallAfterServerConnectionError()).thenReturn(fallback);

        List<Employee> result = employeeService.getAllEmployees();
        assertThat(result).containsExactlyElementsOf(fallbackEmployees);
    }

    @Test
    void findEmployeesByName_shouldFilterCorrectly() {
        List<Employee> employees = EmployeeTestDataGenerator.generateEmployees(5);

        employees.get(0).setEmployee_name("Alice");
        EmployeeResponse response = new EmployeeResponse(employees, "OK");

        when(apiCalls.getAllEmployeesApiCall()).thenReturn(response);

        List<Employee> result = employeeService.findEmployeesByName("ali");
        assertThat(result).containsExactly(employees.get(0));
    }

    @Test
    void findById_shouldReturnEmployee_whenFound() {
        Employee e = EmployeeTestDataGenerator.generateEmployees(1).get(0);
        SoloEmployeeResponse response = new SoloEmployeeResponse(e, "OK");

        when(apiCalls.getEmployeeByID(e.getId())).thenReturn(response);

        Optional<Employee> result = employeeService.findById(e.getId());
        assertThat(result).isPresent().contains(e);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        when(apiCalls.getEmployeeByID("non-existent")).thenReturn(null);

        Optional<Employee> result = employeeService.findById("non-existent");
        assertThat(result).isEmpty();
    }

    @Test
    void getHighestSalary_shouldReturnMaxSalary() {
        List<Employee> employees = EmployeeTestDataGenerator.generateEmployees(5);
        employees.get(2).setEmployee_salary(150_000);
        EmployeeResponse response = new EmployeeResponse(employees, "OK");

        when(apiCalls.getAllEmployeesApiCall()).thenReturn(response);

        int highest = employeeService.getHighestSalary();
        assertThat(highest).isEqualTo(150_000);
    }

    @Test
    void getHighestSalary_shouldReturnZero_whenNoEmployees() {
        when(apiCalls.getAllEmployeesApiCall()).thenReturn(new EmployeeResponse(List.of(), "OK"));
        assertThat(employeeService.getHighestSalary()).isZero();
    }

    @Test
    void getTopTenHighestEarningNames_shouldReturnSortedNames() {
        List<Employee> employees = EmployeeTestDataGenerator.generateEmployees(15);
        for (int i = 0; i < employees.size(); i++) {
            employees.get(i).setEmployee_salary(10_000 + i * 1_000);
        }
        EmployeeResponse response = new EmployeeResponse(employees, "OK");
        when(apiCalls.getAllEmployeesApiCall()).thenReturn(response);

        List<String> topNames = employeeService.getTopTenHighestEarningNames();
        assertThat(topNames).hasSize(10);
        assertThat(topNames.get(0)).isEqualTo(employees.get(14).getEmployee_name());
    }

    @Test
    void createEmployee_shouldReturnCreatedEmployee() {
        EmployeeInput input = new EmployeeInput("Alice", 50000, 30, "Dev");
        Employee emp = EmployeeTestDataGenerator.generateEmployees(1).get(0);
        emp.setEmployee_name("Alice");
        SoloEmployeeResponse response = new SoloEmployeeResponse(emp, "CREATED");

        when(apiCalls.createEmployeeApiCall(input)).thenReturn(response);

        Employee result = employeeService.createEmployee(input);
        assertThat(result).isEqualTo(emp);
    }

    @Test
    void createEmployee_shouldThrowException_whenResponseNull() {
        EmployeeInput input = new EmployeeInput("Alice", 50000, 30, "Dev");
        when(apiCalls.createEmployeeApiCall(input)).thenReturn(null);

        assertThatThrownBy(() -> employeeService.createEmployee(input))
                .isInstanceOf(EmployeeServiceException.class)
                .hasMessageContaining("Employee creation failed");
    }

    @Test
    void deleteEmployee_shouldReturnStatus_whenSuccess() {
        Employee emp = EmployeeTestDataGenerator.generateEmployees(1).get(0);
        EmployeeResponse allEmployees = new EmployeeResponse(List.of(emp), "OK");
        DeleteEmployeeResponse deleteResponse = new DeleteEmployeeResponse(true, "DELETED");

        when(apiCalls.getAllEmployeesApiCall()).thenReturn(allEmployees);
        when(apiCalls.deleteEmployeeApiCall(Map.of("name", emp.getEmployee_name())))
                .thenReturn(deleteResponse);

        String status = employeeService.deleteEmployee(emp.getId());
        assertThat(status).isEqualTo("DELETED");
    }

    @Test
    void deleteEmployee_shouldThrowException_whenEmployeeNotFound() {
        when(apiCalls.getAllEmployeesApiCall()).thenReturn(new EmployeeResponse(List.of(), "OK"));

        assertThatThrownBy(() -> employeeService.deleteEmployee("non-existent"))
                .isInstanceOf(EmployeeServiceException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void deleteEmployee_shouldThrowException_whenDeleteFails() {
        Employee emp = EmployeeTestDataGenerator.generateEmployees(1).get(0);
        EmployeeResponse allEmployees = new EmployeeResponse(List.of(emp), "OK");
        DeleteEmployeeResponse deleteResponse = new DeleteEmployeeResponse(false, "FAILED");

        when(apiCalls.getAllEmployeesApiCall()).thenReturn(allEmployees);
        when(apiCalls.deleteEmployeeApiCall(Map.of("name", emp.getEmployee_name())))
                .thenReturn(deleteResponse);

        assertThatThrownBy(() -> employeeService.deleteEmployee(emp.getId()))
                .isInstanceOf(EmployeeServiceException.class)
                .hasMessageContaining("Failed to delete");
    }
}
