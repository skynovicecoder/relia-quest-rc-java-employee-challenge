package com.reliaquest.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        Employee employee = new Employee("1", "John Doe", 50000, 30, "Developer", "john.doe@example.com");

        assertThat(employee.getId()).isEqualTo("1");
        assertThat(employee.getEmployee_name()).isEqualTo("John Doe");
        assertThat(employee.getEmployee_salary()).isEqualTo(50000);
        assertThat(employee.getEmployee_age()).isEqualTo(30);
        assertThat(employee.getEmployee_title()).isEqualTo("Developer");
        assertThat(employee.getEmployee_email()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        Employee employee = new Employee();
        employee.setId("2");
        employee.setEmployee_name("Jane Doe");
        employee.setEmployee_salary(60000);
        employee.setEmployee_age(28);
        employee.setEmployee_title("Manager");
        employee.setEmployee_email("jane.doe@example.com");

        assertThat(employee.getId()).isEqualTo("2");
        assertThat(employee.getEmployee_name()).isEqualTo("Jane Doe");
        assertThat(employee.getEmployee_salary()).isEqualTo(60000);
        assertThat(employee.getEmployee_age()).isEqualTo(28);
        assertThat(employee.getEmployee_title()).isEqualTo("Manager");
        assertThat(employee.getEmployee_email()).isEqualTo("jane.doe@example.com");
    }

    @Test
    void testEqualsAndHashCode() {
        Employee e1 = new Employee("1", "John Doe", 50000, 30, "Developer", "john.doe@example.com");
        Employee e2 = new Employee("1", "John Doe", 50000, 30, "Developer", "john.doe@example.com");

        assertThat(e1).isEqualTo(e2);
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        Employee employee = new Employee("1", "John Doe", 50000, 30, "Developer", "john.doe@example.com");

        String str = employee.toString();

        assertThat(str)
                .contains("1")
                .contains("John Doe")
                .contains("50000")
                .contains("30")
                .contains("Developer")
                .contains("john.doe@example.com");
    }
}
