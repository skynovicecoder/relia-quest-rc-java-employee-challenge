package com.reliaquest.api.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TooManyRequestsExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Too many requests, please try again later";

        TooManyRequestsException ex = new TooManyRequestsException(message);

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    void testExceptionWithoutMessage() {
        TooManyRequestsException ex = new TooManyRequestsException(null);

        assertThat(ex.getMessage()).isNull();
    }
}
