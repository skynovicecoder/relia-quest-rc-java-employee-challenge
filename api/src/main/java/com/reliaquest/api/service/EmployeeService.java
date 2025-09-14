package com.reliaquest.api.service;

import com.reliaquest.api.dto.*;
import com.reliaquest.api.exception.EmployeeServiceException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

@Service
@Slf4j
public class EmployeeService {
    private final EmployeeServerApiCalls employeeServerApiCalls;

    @Autowired
    public EmployeeService(EmployeeServerApiCalls employeeServerApiCalls) {
        this.employeeServerApiCalls = employeeServerApiCalls;
    }

    public List<Employee> getAllEmployees() {
        EmployeeResponse response = null;
        try {
            log.debug("EmployeeService :: getAllEmployees");
            response = employeeServerApiCalls.getAllEmployeesApiCall();
        } catch (ResourceAccessException conEx) {
            log.error("EmployeeService :: getAllEmployees :: ConnectException");
            response = employeeServerApiCalls.getAllEmployeesApiCallAfterServerConnectionError();
        }
        log.info("EmployeeService :: getAllEmployees :: response : {}", response);
        return response != null ? response.getData() : List.of();
    }

    public List<Employee> findEmployeesByName(String searchString) {
        EmployeeResponse response = null;
        try {
            response = employeeServerApiCalls.getAllEmployeesApiCall();
        } catch (ResourceAccessException conEx) {
            response = employeeServerApiCalls.getAllEmployeesApiCallAfterServerConnectionError();
        }
        if (response == null || response.getData() == null) {
            return List.of();
        }

        String lowerCaseSearch = searchString.toLowerCase(Locale.ROOT);
        List<Employee> employeeList = response.getData().stream()
                .filter(emp -> emp.getEmployee_name() != null
                        && emp.getEmployee_name().toLowerCase(Locale.ROOT).contains(lowerCaseSearch))
                .toList();
        log.info(
                "EmployeeService :: findEmployeesByName :: by searchString :: {} :: response is : {}",
                searchString,
                employeeList);
        return employeeList;
    }

    public Optional<Employee> findById(String id) {
        SoloEmployeeResponse response = null;
        try {
            response = employeeServerApiCalls.getEmployeeByID(id);
        } catch (ResourceAccessException conEx) {
            response = employeeServerApiCalls.getEmployeeByID(id);
        }

        Optional<Employee> employee = Optional.ofNullable(response != null ? response.getData() : null);
        log.info("EmployeeService :: findById :: {} :: response is : {}", id, employee.orElse(new Employee()));
        return employee;
    }

    public Integer getHighestSalary() {
        EmployeeResponse response = null;
        try {
            response = employeeServerApiCalls.getAllEmployeesApiCall();
        } catch (ResourceAccessException conEx) {
            response = employeeServerApiCalls.getAllEmployeesApiCallAfterServerConnectionError();
        }
        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            return 0;
        }

        Integer highestSalary = response.getData().stream()
                .map(Employee::getEmployee_salary)
                .filter(salary -> salary != null)
                .max(Comparator.naturalOrder())
                .orElse(0);
        log.info("EmployeeService :: getHighestSalary :: response is : {}", highestSalary);
        return highestSalary;
    }

    public List<String> getTopTenHighestEarningNames() {
        EmployeeResponse response = null;
        try {
            response = employeeServerApiCalls.getAllEmployeesApiCall();
        } catch (ResourceAccessException conEx) {
            response = employeeServerApiCalls.getAllEmployeesApiCallAfterServerConnectionError();
        }
        if (response == null || response.getData() == null) {
            return List.of();
        }
        List<String> empNameList = response.getData().stream()
                .sorted(Comparator.comparing(
                        Employee::getEmployee_salary, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .map(Employee::getEmployee_name)
                .collect(Collectors.toList());
        log.info("EmployeeService :: getTopTenHighestEarningNames :: response is : {}", empNameList);

        return empNameList;
    }

    public Employee createEmployee(EmployeeInput employeeInput) {
        SoloEmployeeResponse response = null;
        try {
            response = employeeServerApiCalls.createEmployeeApiCall(employeeInput);
        } catch (ResourceAccessException conEx) {
            response = employeeServerApiCalls.createEmployeeApiCallAfterServerConnectionError(employeeInput);
        }
        if (response != null) {
            log.info(
                    "EmployeeService :: createEmployee :: success created employee with name : {}",
                    response.getData().getEmployee_name());
            return response.getData();
        } else {
            log.info(
                    "EmployeeService :: createEmployee :: failed to create employee with name: {}",
                    employeeInput.getName());
            throw new EmployeeServiceException("Employee creation failed: empty response");
        }
    }

    public String deleteEmployee(String id) {
        EmployeeResponse allEmployees = null;
        try {
            allEmployees = employeeServerApiCalls.getAllEmployeesApiCall();
        } catch (ResourceAccessException conEx) {
            allEmployees = employeeServerApiCalls.getAllEmployeesApiCallAfterServerConnectionError();
        }
        Employee employeeToDelete = allEmployees.getData().stream()
                .filter(emp -> id.equals(emp.getId()))
                .findFirst()
                .orElseThrow(() -> new EmployeeServiceException("Employee with id " + id + " not found"));

        Map<String, String> requestBody = Map.of("name", employeeToDelete.getEmployee_name());

        DeleteEmployeeResponse response = null;
        try {
            response = employeeServerApiCalls.deleteEmployeeApiCall(requestBody);
        } catch (ResourceAccessException conEx) {
            response = employeeServerApiCalls.deleteEmployeeApiCall(requestBody);
        }

        if (response != null && Boolean.TRUE.equals(response.getData())) {
            log.info("EmployeeService :: deleteEmployee :: success delete employee with ID : {}", id);
            return response.getStatus();
        } else {
            log.info("EmployeeService :: deleteEmployee :: failed to delete employee with ID : {}", id);
            throw new EmployeeServiceException("Failed to delete employee: " + employeeToDelete.getEmployee_name());
        }
    }
}
