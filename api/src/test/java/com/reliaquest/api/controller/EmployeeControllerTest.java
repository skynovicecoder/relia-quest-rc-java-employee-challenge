package com.reliaquest.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.dto.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import com.reliaquest.api.util.EmployeeTestDataGenerator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    private List<Employee> mockEmployees;

    @BeforeEach
    void setUp() {
        mockEmployees = EmployeeTestDataGenerator.generateEmployees(20);
    }

    @Test
    void getAllEmployees_ReturnsEmployeeList() {
        when(employeeService.getAllEmployees()).thenReturn(mockEmployees);

        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(20).isEqualTo(mockEmployees);

        verify(employeeService).getAllEmployees();
    }

    @Test
    void getEmployeesByNameSearch_ReturnsFilteredEmployees() {
        String searchString = mockEmployees.get(0).getEmployee_name().split(" ")[0];
        List<Employee> filtered = mockEmployees.stream()
                .filter(emp -> emp.getEmployee_name().contains(searchString))
                .collect(Collectors.toList());

        when(employeeService.findEmployeesByName(searchString)).thenReturn(filtered);

        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch(searchString);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(filtered);

        verify(employeeService).findEmployeesByName(searchString);
    }

    @Test
    void getEmployeeById_WhenEmployeeExists_ReturnsEmployee() {
        Employee emp = mockEmployees.get(0);
        when(employeeService.findById(emp.getId())).thenReturn(Optional.of(emp));

        ResponseEntity<Employee> response = employeeController.getEmployeeById(emp.getId());

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(emp);

        verify(employeeService).findById(emp.getId());
    }

    @Test
    void getEmployeeById_WhenEmployeeDoesNotExist_ReturnsNotFound() {
        String id = "non-existent";
        when(employeeService.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Employee> response = employeeController.getEmployeeById(id);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNull();

        verify(employeeService).findById(id);
    }

    @Test
    void getHighestSalaryOfEmployees_ReturnsHighestSalary() {
        int maxSalary = 150_000;
        when(employeeService.getHighestSalary()).thenReturn(maxSalary);

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        assertThat(response.getBody()).isEqualTo(maxSalary);
        verify(employeeService).getHighestSalary();
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ReturnsNames() {
        List<String> topNames = List.of("John", "Jane", "Alice", "Bob");
        when(employeeService.getTopTenHighestEarningNames()).thenReturn(topNames);

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        assertThat(response.getBody()).isEqualTo(topNames);
        verify(employeeService).getTopTenHighestEarningNames();
    }

    @Test
    void createEmployee_ReturnsCreatedEmployee() {
        EmployeeInput input = new EmployeeInput("John Doe", 50_000, 30, "Developer");
        Employee created = new Employee("1", "John Doe", 50_000, 30, "Developer", "john@example.com");
        when(employeeService.createEmployee(input)).thenReturn(created);

        ResponseEntity<Employee> response = employeeController.createEmployee(input);

        assertThat(response.getBody()).isEqualTo(created);
        verify(employeeService).createEmployee(input);
    }

    @Test
    void deleteEmployeeById_ReturnsConfirmationMessage() {
        String id = "123";
        when(employeeService.deleteEmployee(id)).thenReturn("Deleted successfully");

        ResponseEntity<String> response = employeeController.deleteEmployeeById(id);

        assertThat(response.getBody()).isEqualTo("Employee deleted with id: " + id);
        verify(employeeService).deleteEmployee(id);
    }
}
