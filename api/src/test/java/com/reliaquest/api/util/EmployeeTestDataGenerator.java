package com.reliaquest.api.util;

import com.reliaquest.api.dto.Employee;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.datafaker.Faker;

public class EmployeeTestDataGenerator {
    private static final Faker faker = new Faker();

    public static List<Employee> generateEmployees(int count) {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Employee emp = new Employee(
                    UUID.randomUUID().toString(),
                    faker.name().fullName(),
                    faker.number().numberBetween(30000, 150000),
                    faker.number().numberBetween(20, 65),
                    faker.job().position(),
                    faker.internet().emailAddress());
            employees.add(emp);
        }
        return employees;
    }
}
