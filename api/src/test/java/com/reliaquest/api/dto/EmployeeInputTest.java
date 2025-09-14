package com.reliaquest.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmployeeInputTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        EmployeeInput input = new EmployeeInput("John Doe", 50000, 30, "Developer");

        assertThat(input.getName()).isEqualTo("John Doe");
        assertThat(input.getSalary()).isEqualTo(50000);
        assertThat(input.getAge()).isEqualTo(30);
        assertThat(input.getTitle()).isEqualTo("Developer");
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        EmployeeInput input = new EmployeeInput();
        input.setName("Jane Doe");
        input.setSalary(60000);
        input.setAge(28);
        input.setTitle("Manager");

        assertThat(input.getName()).isEqualTo("Jane Doe");
        assertThat(input.getSalary()).isEqualTo(60000);
        assertThat(input.getAge()).isEqualTo(28);
        assertThat(input.getTitle()).isEqualTo("Manager");
    }

    @Test
    void testEqualsAndHashCode() {
        EmployeeInput e1 = new EmployeeInput("John Doe", 50000, 30, "Developer");
        EmployeeInput e2 = new EmployeeInput("John Doe", 50000, 30, "Developer");

        assertThat(e1).isEqualTo(e2);
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        EmployeeInput input = new EmployeeInput("John Doe", 50000, 30, "Developer");

        String str = input.toString();

        assertThat(str).contains("John Doe").contains("50000").contains("30").contains("Developer");
    }
}
