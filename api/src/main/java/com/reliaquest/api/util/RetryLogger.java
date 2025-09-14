package com.reliaquest.api.util;

import io.github.resilience4j.retry.event.RetryOnRetryEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RetryLogger {

    private final io.github.resilience4j.retry.RetryRegistry retryRegistry;

    public RetryLogger(io.github.resilience4j.retry.RetryRegistry retryRegistry) {
        this.retryRegistry = retryRegistry;
    }

    @PostConstruct
    public void registerRetryListeners() {
        retryRegistry.getAllRetries().forEach(retry -> retry.getEventPublisher().onRetry(this::logRetryEvent));
    }

    void logRetryEvent(RetryOnRetryEvent event) {
        log.warn(
                "Retry '{}' attempt #{}, last exception: {}, waiting {} seconds before next retry",
                event.getName(),
                event.getNumberOfRetryAttempts(),
                event.getLastThrowable() != null ? event.getLastThrowable().toString() : "n/a",
                event.getWaitInterval().getSeconds());
    }
}
