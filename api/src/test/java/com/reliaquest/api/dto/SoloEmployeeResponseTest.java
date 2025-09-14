package com.reliaquest.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SoloEmployeeResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        Employee employee = new Employee("1", "John Doe", 50000, 30, "Developer", "john@example.com");
        SoloEmployeeResponse response = new SoloEmployeeResponse(employee, "success");

        assertThat(response.getData()).isEqualTo(employee);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        Employee employee = new Employee("2", "Jane Doe", 60000, 28, "Manager", "jane@example.com");

        SoloEmployeeResponse response = new SoloEmployeeResponse();
        response.setData(employee);
        response.setStatus("ok");

        assertThat(response.getData()).isEqualTo(employee);
        assertThat(response.getStatus()).isEqualTo("ok");
    }

    @Test
    void testEqualsAndHashCode() {
        Employee employee = new Employee("1", "John Doe", 50000, 30, "Developer", "john@example.com");

        SoloEmployeeResponse r1 = new SoloEmployeeResponse(employee, "success");
        SoloEmployeeResponse r2 = new SoloEmployeeResponse(employee, "success");

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        Employee employee = new Employee("1", "John Doe", 50000, 30, "Developer", "john@example.com");
        SoloEmployeeResponse response = new SoloEmployeeResponse(employee, "success");

        String str = response.toString();

        assertThat(str).contains("John Doe").contains("50000").contains("success");
    }
}
