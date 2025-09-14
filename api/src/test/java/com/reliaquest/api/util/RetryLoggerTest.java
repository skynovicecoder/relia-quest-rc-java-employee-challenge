package com.reliaquest.api.util;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.event.RetryOnRetryEvent;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class RetryLoggerTest {

    private RetryLogger retryLogger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setup() {
        RetryRegistry retryRegistry = RetryRegistry.ofDefaults();
        retryLogger = new RetryLogger(retryRegistry);

        Logger logger = (Logger) LoggerFactory.getLogger(RetryLogger.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void testLogRetryEvent_withThrowable_logsCorrectly() {
        RetryOnRetryEvent event = new RetryOnRetryEvent("myRetry", 2, new IOException("network error"), 0L);

        retryLogger.logRetryEvent(event);

        assertThat(listAppender.list).anySatisfy(logEvent -> {
            assertThat(logEvent.getLevel()).isEqualTo(Level.WARN);
            assertThat(logEvent.getFormattedMessage())
                    .contains("Retry 'myRetry' attempt #2, last exception: java.io.IOException: network error");
        });
    }

    @Test
    void testLogRetryEvent_withoutThrowable_logsNA() {
        RetryOnRetryEvent event = new RetryOnRetryEvent("myRetry", 1, null, 0L);

        retryLogger.logRetryEvent(event);

        assertThat(listAppender.list).anySatisfy(logEvent -> {
            assertThat(logEvent.getLevel()).isEqualTo(Level.WARN);
            assertThat(logEvent.getFormattedMessage()).contains("Retry 'myRetry' attempt #1, last exception: n/a");
        });
    }
}
