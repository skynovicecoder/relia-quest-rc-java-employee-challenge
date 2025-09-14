package com.reliaquest.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DeleteEmployeeResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        DeleteEmployeeResponse response = new DeleteEmployeeResponse(true, "SUCCESS");

        assertThat(response.getData()).isTrue();
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        DeleteEmployeeResponse response = new DeleteEmployeeResponse();
        response.setData(false);
        response.setStatus("FAILED");

        assertThat(response.getData()).isFalse();
        assertThat(response.getStatus()).isEqualTo("FAILED");
    }

    @Test
    void testEqualsAndHashCode() {
        DeleteEmployeeResponse r1 = new DeleteEmployeeResponse(true, "SUCCESS");
        DeleteEmployeeResponse r2 = new DeleteEmployeeResponse(true, "SUCCESS");

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        DeleteEmployeeResponse response = new DeleteEmployeeResponse(true, "SUCCESS");

        assertThat(response.toString()).contains("true").contains("SUCCESS");
    }
}
