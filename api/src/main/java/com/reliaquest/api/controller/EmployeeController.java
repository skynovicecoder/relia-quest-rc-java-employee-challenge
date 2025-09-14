package com.reliaquest.api.controller;

import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.dto.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
@Slf4j
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.debug("EmployeeController :: Inside getAllEmployees");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        log.debug("EmployeeController :: Inside getEmployeesByNameSearch :: Search By Name String : {}", searchString);
        return ResponseEntity.ok(employeeService.findEmployeesByName(searchString));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        log.debug("EmployeeController :: Inside getEmployeeById :: Input ID:: {}", id);
        return ResponseEntity.of(employeeService.findById(id));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.debug("EmployeeController :: Inside getHighestSalaryOfEmployees");
        return ResponseEntity.ok(employeeService.getHighestSalary());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.debug("EmployeeController :: Inside getTopTenHighestEarningEmployeeNames");
        return ResponseEntity.ok(employeeService.getTopTenHighestEarningNames());
    }

    @Override
    public ResponseEntity<Employee> createEmployee(EmployeeInput employeeInput) {
        log.debug("EmployeeController :: Inside createEmployee :: Input Object:: {}", employeeInput);
        return ResponseEntity.ok(employeeService.createEmployee(employeeInput));
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        log.debug("EmployeeController :: Inside deleteEmployeeById :: Delete ID:: {}", id);
        String response = employeeService.deleteEmployee(id);
        log.debug("Status:: {}", response);
        return ResponseEntity.ok("Employee deleted with id: " + id);
    }
}
